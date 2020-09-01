package eps.platform.infraestructure;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import eps.platform.infraestructure.cli.ApplicationCLI;
import eps.platform.infraestructure.common.Utils;
import eps.platform.infraestructure.config.YamlConfig;
import eps.platform.infraestructure.ems.tibco.ConnectThread;
import eps.platform.infraestructure.ems.tibco.EmsConfiguration;
import eps.platform.infraestructure.ems.tibco.EmsStats;
import eps.platform.infraestructure.ems.tibco.EmsStatsLogger;
import eps.platform.infraestructure.ems.tibco.StatsCollection;
import eps.platform.infraestructure.exception.EPSMonioringException;
import eps.platform.infraestructure.exception.ErrorCode;
import eps.platform.infraestructure.nmon.csv.InMemoryDB;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EMS2JSON {
    private static final Logger logSTDOUT = LoggerFactory.getLogger("STDOUT");
	
	public static final String APPPLICATION = "EMS2JSON";	
	public static final String wlDateFormat = "yyyy-MM-dd";	
	public static final DateTimeFormatter wlDateFormatter = DateTimeFormatter.ofPattern(wlDateFormat);
	
	private static YamlConfig yamlConfig;

	// Process arguments
	private static ApplicationCLI applicationCLI = new ApplicationCLI();
	
	// Assumptions:
	// Folder structure: DATA_CENTRE | YYYY-MM | YYYY-MM-DD | FILE
	// FILE NAME: HOSTNAME_YYMMDD_00000.mon
	public static void main(String[] args) {

		BlockingQueue<StatsCollection> queue = new LinkedBlockingQueue<>();
		
		Instant start = Instant.now();
		int filesProcessed = 0;
				
		// Process arguments
		boolean checkConfig = applicationCLI.checkConfig(args);
		if (!checkConfig) {
			logSTDOUT.error(ApplicationCLI.printHelp());
			System.exit(-1);
		}

		if (applicationCLI.isSwHelp()) {
			logSTDOUT.info(ApplicationCLI.printHelp());
			System.exit(0);
		}

		// Load Reference data
		try {
			loadReference(applicationCLI);
		} catch (EPSMonioringException e1) {
			log.error("Error reference .csv file: " + applicationCLI.getReferenceCSVPath());
			System.exit(-1);
		}
		
		// Load ems configuration from yaml file 
		try {
			yamlConfig = initServers();
			
			if (yamlConfig == null) {
				log.error("Error reading Servers.yaml file: " + applicationCLI.getServersFilePath());
				System.exit(-1);				
			}
			log.debug(yamlConfig.toString());
		} catch (Exception e) {
			log.error(String.format("Error parsing Servers.yaml file: %s%n%s", applicationCLI.getServersFilePath(), e.getLocalizedMessage()));
			System.exit(-1);
		}
		
		// Convert yaml structure to List
		EmsConfiguration.configure(yamlConfig.getServers());
		
		//
		EmsStats.init();
		
		// Start schedulled task to keep connections open
		ConnectThread connectThread = new ConnectThread();
		ScheduledExecutorService executorConnectThread = Executors.newSingleThreadScheduledExecutor();
		ScheduledFuture<?> resultConnectThread = executorConnectThread.scheduleAtFixedRate(connectThread, 5, 5, TimeUnit.SECONDS);
		
		// Start schedulled task to keep query stats
		EmsStatsLogger emsStatsLogger = new EmsStatsLogger(queue);
		ScheduledExecutorService executorEmsStatsLogger = Executors.newSingleThreadScheduledExecutor();
		ScheduledFuture<?> resultEmsStatsLogger = executorEmsStatsLogger.scheduleWithFixedDelay(emsStatsLogger, 5, applicationCLI.getInterval(), TimeUnit.SECONDS);		
		
		// Start schedulled task to export stats to JSON
		EmsStatsToJSON emsStatsToJSON = new EmsStatsToJSON(queue, applicationCLI.isSwCSV());
		ExecutorService executorEmsStatsToJSON = Executors.newSingleThreadScheduledExecutor();
		Future<?> resultEmsStatsToJSON = executorEmsStatsToJSON.submit(emsStatsToJSON);		
		
		// Install shutdown hook
	    CountDownLatch doneSignal = new CountDownLatch(1);

	    Runtime.getRuntime().addShutdownHook(new Thread() {

	        /** This handler will be called on Control-C pressed */
	        @Override
	        public void run() {
	            // Decrement counter.
	            // It will became 0 and main thread who waits for this barrier could continue run (and fulfill all proper shutdown steps)
	            doneSignal.countDown();
	        }
	    });
	    
	    // Here we enter wait state until control-c will be pressed
	    try {
	        doneSignal.await();
	    } catch (InterruptedException e) {
	    }
	    
		Instant end = Instant.now();
		Duration duration = Duration.between(start, end);
		
		log.info(StringUtils.repeat('-', 80));
		log.info("End process");
		log.info(StringUtils.repeat('-', 80));
		log.info("Number of files processed: {}", filesProcessed);
		log.info("Total time: {} s", duration.getSeconds());
		log.info(StringUtils.repeat('-', 80));
		
	    log.info("Shutting down");
	    log.info("Closing connections");
	    
	    if (resultConnectThread != null) {
		    log.info("Stoping Connection thread");	    	
	    	resultConnectThread.cancel(true);
	    }
	    
	    if (resultConnectThread != null) {	    
		    log.info("Stoping Logging thread");
			resultEmsStatsLogger.cancel(true);
	    }

	    if (resultEmsStatsToJSON != null) {	    
		    log.info("Stoping to JSON thread");
		    executorEmsStatsToJSON.shutdown();
	    }
	    
	    log.info("Shutdown complete");
		
	}
	
	/**
	 * Load Servers yaml file
	 * 
	 * @return
	 * @throws Exception
	 */
	public static YamlConfig initServers() throws Exception {
		
		Yaml yaml = new Yaml(new Constructor(YamlConfig.class));
		
		// Check if the file exists
	    Path filePath= applicationCLI.getServersFilePath();
	    if (!Utils.validateAccessToFile(filePath)) {
	    	return null;
	    }
	 		
	    String data = new String(Files.readAllBytes(filePath));	    

	    // Load the yaml file
	    yamlConfig = yaml.load(data);
	    return yamlConfig;
	}
	
	public static void loadReference(ApplicationCLI applicationCLI) throws EPSMonioringException {
		if (applicationCLI.getReferenceCSVPath() != null) {
			// Load reference
			try {
				InMemoryDB.loadReferenceCSV(applicationCLI.getReferenceCSVPath());
				log.info("Infrastructure reference CSV loaded [{}]", applicationCLI.getReferenceCSVPath());
			} catch (Exception e) {
				throw new EPSMonioringException(String.format("Error processing infrastructure reference CSV %s", applicationCLI.getReferenceCSVPath()), 
						e, 
						ErrorCode.INVALID_REFERECENCE_CSV);
			}
		}		
	}
	
}

