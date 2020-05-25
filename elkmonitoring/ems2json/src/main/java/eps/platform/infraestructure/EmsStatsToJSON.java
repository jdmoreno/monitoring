package eps.platform.infraestructure;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eps.platform.infraestructure.ems.stats.StatsDestination;
import eps.platform.infraestructure.ems.stats.StatsServer;
import eps.platform.infraestructure.ems.tibco.StatsCollection;
import eps.platform.infraestructure.json.dto.ems.DestinationInfoDto;
import eps.platform.infraestructure.json.dto.ems.ServerInfoDto;
import eps.platform.infraestructure.json.dto.nmon.HeaderDto;
import eps.platform.infraestructure.json.ser.ems.DestinationInfoSerialiser;
import eps.platform.infraestructure.json.ser.ems.ServerInfoSerialiser;
import eps.platform.infraestructure.json.ser.nmon.HeaderDto_TypeAdapter;
import eps.platform.infraestructure.nmon.csv.InMemoryDB;
import eps.platform.infraestructure.nmon.csv.Reference;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmsStatsToJSON implements Runnable {
    private final BlockingQueue<StatsCollection> queue;	
    private static final Logger logJSON = LoggerFactory.getLogger("jsonLogger");
    
	private static Gson gson = null;
    
	public EmsStatsToJSON(BlockingQueue<StatsCollection> queue) {
		this.queue = queue;		
	}

	static {
		GsonBuilder builder = new GsonBuilder();
		builder.excludeFieldsWithoutExposeAnnotation();
		builder.registerTypeAdapter(HeaderDto.class, new HeaderDto_TypeAdapter());		
		builder.registerTypeAdapter(DestinationInfoDto.class, new DestinationInfoSerialiser());
		builder.registerTypeAdapter(ServerInfoDto.class, new ServerInfoSerialiser());
		EmsStatsToJSON.gson = builder.create();		
	}
	
	@Override
	public void run() {		
        try {
            while (true) {
                StatsCollection statsCollection = queue.take();
                process(statsCollection);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }		
	}
	
    private void process(StatsCollection statsCollection) throws InterruptedException {
    	log.debug("Start logging to JSON statsCollection ...");
    	if (statsCollection != null) {
    		
    		// Server stats (queues/topics)
	    	for (StatsServer statsServer : statsCollection.getStatsServers()) {
	    		log.debug("Starting logging to JSON server: {} - hostname: {} - url: {}", statsServer.getName(), statsServer.getHostname(), statsServer.getUrl() );
	    		// Common header
	    		Reference reference = InMemoryDB.get(statsServer.getHostname());		
	    		
	    		String dataCentre = "";
	    		String dataEnvironment = "";
	    		String dataServerFunction = "";
	    		if (reference != null) {
		    		dataCentre = reference.getDataCentre();
		    		dataEnvironment = reference.getEnvironment();
		    		dataServerFunction = reference.getServerFunction();	    			
	    		}
	    		HeaderDto header = HeaderDto.builder()
	    			.timestamp(statsServer.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
	    			.hostname(statsServer.getHostname())
//	    			.os(this.os)
	    			.dataCentre(dataCentre)
	    			.environment(dataEnvironment)
	    			.serverFunction(dataServerFunction)
	    			.build();
				
	    		ServerInfoDto serverInfoDto = ServerInfoDto.builder()
	    				.header(header)
	    				.serverName(statsServer.getName())
	    				.hostname(statsServer.getHostname())
	    				.url(statsServer.getUrl())
	    				.build();
	    		serverInfoDto.getStats().putAll(statsServer.getStats());
	    		
    			try {
    				logJSON.info(gson.toJson(serverInfoDto));					
				} catch (Exception e) {
					log.error("Exception transforming server stats to JSON - Stat info {}", serverInfoDto.toString());
					log.error("Exception transforming server stats to JSON - Exception info {}", e.getMessage());
				}
	    			    
	    		// Destination stats (queues/topics). Stats of the destinations associated with the curent server being processed
	    		for (StatsDestination statsDestination : statsServer.getDestinations()) {
	    			log.debug("Starting logging to JSON destination - server: {} - hostname: {} - url: {} - destination: {}", statsServer.getName(), statsServer.getHostname(), statsServer.getUrl(), statsDestination.getName());
	    			DestinationInfoDto destinationInfoDto = DestinationInfoDto.builder()
	    					.header(header)
	    					.serverName(statsServer.getName())
	    					.hostname(statsServer.getHostname())
	    					.destinationName(statsDestination.getName())
	    					.destinationType(statsDestination.getDestinationType().toString())
	    					.store(statsDestination.getStore())
	    					.staticSw(statsDestination.isStaticSw())
	    					.temporary(statsDestination.isTemporary())
	    					.build();
	    			destinationInfoDto.getStats().putAll(statsDestination.getStats());
	    			destinationInfoDto.getStatsInbound().putAll(statsDestination.getStatsInbound());
	    			destinationInfoDto.getStatsOutbound().putAll(statsDestination.getStatsOutbound());
	    			
	    			try {
		    			logJSON.info(gson.toJson(destinationInfoDto));					
					} catch (Exception e) {
						log.error("Exception transforming destination stats to JSON - Stat info {}", destinationInfoDto.toString());
						log.error("Exception transforming destination stats to JSON - Exception info {}", e.getCause());
					}
	    			
	    			log.debug("End logging to JSON destination - server: {} - hostname: {} - url: {} - destination: {}", statsServer.getName(), statsServer.getHostname(), statsServer.getUrl(), statsDestination.getName());
				}
	    		log.debug("End logging to JSON server: {} - hostname: {} - url: {}", statsServer.getName(), statsServer.getHostname(), statsServer.getUrl() );
	    	}    	
    	}
    	log.debug("End logging to JOSN statsCollection");
        Thread.sleep(500);
    }
    
}
