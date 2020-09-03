package eps.platform.infraestructure.ems.tibco;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eps.platform.infraestructure.DestinationType;
import eps.platform.infraestructure.common.CSVSerializer;
import eps.platform.infraestructure.common.Common;
import eps.platform.infraestructure.common.EmsStatNames;
import eps.platform.infraestructure.dto.ems.DestinationInfoDto;
import eps.platform.infraestructure.dto.ems.ServerInfoDto;
import eps.platform.infraestructure.dto.nmon.HeaderDto;
import eps.platform.infraestructure.ems.stats.ListServersStats;
import eps.platform.infraestructure.ems.stats.StatName;
import eps.platform.infraestructure.ems.stats.StatsDestination;
import eps.platform.infraestructure.ems.stats.StatsServer;
import eps.platform.infraestructure.json.ser.ems.DestinationInfoSerialiser;
import eps.platform.infraestructure.json.ser.ems.ServerInfoSerialiser;
import eps.platform.infraestructure.json.ser.nmon.HeaderDto_TypeAdapter;
import eps.platform.infraestructure.nmon.csv.InMemoryDB;
import eps.platform.infraestructure.nmon.csv.Reference;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggerThread implements Runnable {
    private final BlockingQueue<ListServersStats> queue;
	private final boolean swCSV;
	
	private final List<String> serverHeaders = new ArrayList<>();
	private final List<String> queueHeaders = new ArrayList<>();
	private final List<String> topicHeaders = new ArrayList<>();
    
    private static final Logger logJSON = LoggerFactory.getLogger("jsonLogger");
    private static final Logger logCSVServers = LoggerFactory.getLogger("csvLoggerServers");
    private static final Logger logCSVQueues = LoggerFactory.getLogger("csvLoggerQueues");
    private static final Logger logCSVTopics = LoggerFactory.getLogger("csvLoggerTopics");
    
	private static Gson gson = null;
    
	public LoggerThread(BlockingQueue<ListServersStats> queue, EmsStatNames emsStatNames, boolean swCSV) {
		this.queue = queue;		
		this.swCSV = swCSV;		
		
		prepareCSVHeaders(emsStatNames);
		
		log.debug("Server CSV headers: {}", CSVSerializer.iterableToCSV(serverHeaders));
		log.debug("Queue CSV headers: {}", CSVSerializer.iterableToCSV(queueHeaders));
		log.debug("Topic CSV headers: {}", CSVSerializer.iterableToCSV(topicHeaders));
		
		// Log headers to CSV files
		if (swCSV) {
			logCSVServers.info(CSVSerializer.iterableToCSV(serverHeaders));
			logCSVQueues.info(CSVSerializer.iterableToCSV(queueHeaders));
			logCSVTopics.info(CSVSerializer.iterableToCSV(topicHeaders));
		}
	}

	private void prepareCSVHeaders(EmsStatNames emsStatNames) {
		// Prepare Server CSV headers
		serverHeaders.add(Common.CSV_LIT_RECORD_TYPE);
		
		// Common header
		prepareCommonCSVHeader(serverHeaders);
		
		// Server header
		serverHeaders.add(Common.CSV_LIT_EMS_SERVER_NAME);
		serverHeaders.add(Common.CSV_LIT_EMS_HOSTNAME);
		serverHeaders.add(Common.CSV_LIT_URL);

		// Server metrics header
		for (StatName key : emsStatNames.getServerStatsNames().keySet()) {
			serverHeaders.add(key.getDisplayName());
		}

		// Prepare Queue/Topic CSV headers
		for (DestinationType destinationType : DestinationType.values()) {
			
			List<String> headers = null;
			switch (destinationType) {
			case QUEUE:
				headers = queueHeaders; 
				break;

			case TOPIC:
				headers = topicHeaders;
				break;
				
			default:
				continue;
			}
			
			// Record type			
			headers.add(Common.CSV_LIT_RECORD_TYPE);

			// Common header
			prepareCommonCSVHeader(headers);

			// Destination specific headers
			headers.add(Common.CSV_LIT_EMS_SERVER_NAME);
			headers.add(Common.CSV_LIT_EMS_HOSTNAME);

			// Destination metrics headers
			Set<StatName> destTypeSpecHeaders;
			switch (destinationType) {
			case QUEUE:
				destTypeSpecHeaders = emsStatNames.getQueueStatsNames().keySet();
				break;

			case TOPIC:
				destTypeSpecHeaders = emsStatNames.getTopicStatsNames().keySet();
				break;
				
			default:
				continue;
			}
			
			for (StatName key : destTypeSpecHeaders) {
				headers.add(key.getDisplayName());
			}

			// Queue Inbound metrics header
			for (StatName key : emsStatNames.getDestinationInboudStatsNames().keySet()) {
				headers.add(key.getDisplayName());
			}

			// Queue Outbound metrics header
			for (StatName key : emsStatNames.getDestinationOutboundStatsNames().keySet()) {
				headers.add(key.getDisplayName());
			}		
		}
	}

	private static void prepareCommonCSVHeader(List<String> headers) {
		headers.add(Common.CSV_LIT_SOURCE);
		headers.add(Common.CSV_LIT_SECTION);
		headers.add(Common.CSV_LIT_TIMESTAMP);
		headers.add(Common.CSV_LIT_HOSTNAME);
		headers.add(Common.CSV_LIT_OS);
		headers.add(Common.CSV_LIT_DATACENTRE);
		headers.add(Common.CSV_LIT_ENVIRONMENT);
		headers.add(Common.CSV_LIT_SERVER_FUNCTION);
	}

	static {
		GsonBuilder builder = new GsonBuilder();
		builder.excludeFieldsWithoutExposeAnnotation();
		builder.registerTypeAdapter(HeaderDto.class, new HeaderDto_TypeAdapter());		
		builder.registerTypeAdapter(DestinationInfoDto.class, new DestinationInfoSerialiser());
		builder.registerTypeAdapter(ServerInfoDto.class, new ServerInfoSerialiser());
		LoggerThread.gson = builder.create();		
	}
	
	@Override
	public void run() {		
        try {
            while (true) {
                ListServersStats statsCollection = queue.take();
                process(statsCollection, serverHeaders, queueHeaders, topicHeaders, swCSV);
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }		
	}
	
    private static void process(ListServersStats statsCollection, List<String> serverHeaders, List<String> queueHeaders, List<String> topicHeaders, boolean swCSV) throws InterruptedException {
    	log.debug("Start logging ...");
    	if (statsCollection != null) {
    		    		
    		// Server stats (queues/topics)
	    	for (StatsServer statsServer : statsCollection.getStatsServers()) {
	    		log.debug("Starting {} logging server - server: {} - hostname: {} - url: {}", (!swCSV)?"JSON":"CSV", statsServer.getName(), statsServer.getHostname(), statsServer.getUrl() );
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
	    			.source("")
	    			.os("")
	    			.section("")
	    			.dataCentre(dataCentre)
	    			.environment(dataEnvironment)
	    			.serverFunction(dataServerFunction)
	    			.build();
				
	    		// Server stats
	    		ServerInfoDto serverInfoDto = ServerInfoDto.builder()
	    				.header(header)
	    				.serverName(statsServer.getName())
	    				.hostname(statsServer.getHostname())
	    				.url(statsServer.getUrl())
	    				.build();
	    		
	    		// Move server stats to server Dto
	    		for (Entry<StatName, Object> statsEntry : statsServer.getStats().entrySet()) {
	    			serverInfoDto.getStats().put(statsEntry.getKey().getDisplayName(), statsEntry.getValue());
				}
	    		
				try {
					if (!swCSV) {
						logJSON.info(gson.toJson(serverInfoDto));
					} else {
						StringBuilder sb = new StringBuilder();
						CSVSerializer.serialize(serverInfoDto, serverHeaders, sb);
						logCSVServers.info(sb.toString());
					}
				} catch (Exception e) {
					log.error("Exception logging server stats - Stat info {} - Exception info {}",
							serverInfoDto.toString(), e.getMessage());
				}
	    			    
	    		// Destination stats (queues/topics)
				// Stats of the destinations associated with the current server being processed
	    		for (StatsDestination statsDestination : statsServer.getQueuesStats()) {
	    			moveDestinationStats(statsServer, header, statsDestination, logCSVQueues, queueHeaders, swCSV);
				}

	    		for (StatsDestination statsDestination : statsServer.getTopicsStats()) {
	    			moveDestinationStats(statsServer, header, statsDestination, logCSVTopics, topicHeaders, swCSV);
				}
	    		
	    		log.debug("End logging server - server: {} - hostname: {} - url: {}", statsServer.getName(), statsServer.getHostname(), statsServer.getUrl() );
	    	}    	
    	}
    	log.debug("End logging");
    }

	private static void moveDestinationStats(StatsServer statsServer, HeaderDto header, StatsDestination statsDestination, Logger logger, List<String> headers, boolean swCSV) {
		log.debug("Starting {} logging destination - server: {} - hostname: {} - url: {} - destination: {}",
				(!swCSV) ? "JSON" : "CSV", statsServer.getName(), statsServer.getHostname(), statsServer.getUrl(),
				statsDestination.getName());
		DestinationInfoDto destinationInfoDto = DestinationInfoDto.builder().header(header)
				.serverName(statsServer.getName()).hostname(statsServer.getHostname())
				.destinationName(statsDestination.getName())
				.destinationType(statsDestination.getDestinationType().toString()).store(statsDestination.getStore())
				.staticSw(statsDestination.isStaticSw()).temporary(statsDestination.isTemporary()).build();

		for (Entry<StatName, Object> statsEntry : statsDestination.getStats().entrySet()) {
			destinationInfoDto.getStats().put(statsEntry.getKey().getDisplayName(), statsEntry.getValue());
		}
		
		for (Entry<StatName, Object> statsEntry : statsDestination.getStatsInbound().entrySet()) {
			destinationInfoDto.getStats().put(statsEntry.getKey().getDisplayName(), statsEntry.getValue());
		}
		
		for (Entry<StatName, Object> statsEntry : statsDestination.getStatsOutbound().entrySet()) {
			destinationInfoDto.getStats().put(statsEntry.getKey().getDisplayName(), statsEntry.getValue());
		}

		try {
			if (!swCSV) {
				logJSON.info(gson.toJson(destinationInfoDto));
			} else {
				StringBuilder sb = new StringBuilder();
				CSVSerializer.serialize(destinationInfoDto, headers, sb);
				logger.info(sb.toString());

			}
		} catch (Exception e) {
			log.error("Exception logging destination stats - Stat info {} - Exception info {}",
					destinationInfoDto.toString(), e.getCause());
		}
		log.debug("End logging destination - server: {} - hostname: {} - url: {} - destination: {}",
				statsServer.getName(), statsServer.getHostname(), statsServer.getUrl(), statsDestination.getName());
	}
}
