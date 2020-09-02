package eps.platform.infraestructure.ems.tibco;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.tibco.tibjms.admin.DestinationInfo;
import com.tibco.tibjms.admin.ServerInfo;
import com.tibco.tibjms.admin.TibjmsAdmin;
import com.tibco.tibjms.admin.TibjmsAdminException;

import eps.platform.infraestructure.DestinationType;
import eps.platform.infraestructure.config.Queue;
import eps.platform.infraestructure.config.Server;
import eps.platform.infraestructure.config.Topic;
import eps.platform.infraestructure.csv.CSVSerializer;
import eps.platform.infraestructure.ems.stats.DestinationStats;
import eps.platform.infraestructure.ems.stats.StatsServer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class EmsServer {
	private String alias;
	private String url;
	private String user;
	private String password;
	private Map<?,?> sslParams = null;
	
	private final Map<String, EmsDestination> queuePatterns = new HashMap<>();
	private final Map<String, EmsDestination> topicPatterns = new HashMap<>();
		
	@Setter private TibjmsAdmin adminConn = null;	
	@Setter private TibjmsAdmin staleConn = null;	
	@Setter private ServerInfo serverInfo = null;
	
//	// Using LinkedHashSet to maintain insertion order and avoid duplicates
//	private final Set<DestinationInfo> queues = new LinkedHashSet<>();
//	private final Set<DestinationInfo> topics = new LinkedHashSet<>();
	
	public EmsServer(Server yamlServer) {
		this(yamlServer.getAlias(), yamlServer.getUrl(), yamlServer.getUser(), yamlServer.getPassword(), null, null, null);		
		
		queuePatterns.clear();
		for (Queue queue : yamlServer.getQueues()) {
			queuePatterns.put(queue.getPattern(), new EmsDestination(queue));
		}

		topicPatterns.clear();
		for (Topic topic : yamlServer.getTopics()) {
			topicPatterns.put(topic.getPattern(), new EmsDestination(topic));
		}
	}
	
	public EmsServer(String alias, String url, String user, String password, Map<?,?> sslParams, Map<String, EmsDestination> queuesPatterns, Map<String, EmsDestination> topicsPatterns) {
		this.alias = alias;
		this.url = url;
		this.user = user;
		this.password = password;
		this.sslParams = sslParams;
		
		if (queuesPatterns != null) {
			this.queuePatterns.putAll(queuesPatterns);
		}
		
		if (topicsPatterns != null) {
			this.topicPatterns.putAll(topicsPatterns);
		}
	}

	public void close() {
		if (adminConn != null) {
			try {
				adminConn.close();
			} catch (TibjmsAdminException e1) {
			}
		}
		adminConn = null;
	}
	
	public void serverDisconnected(String reason) {
		log.warn("Disconnected from Server {}. Reason: {}", alias, reason);
	}
	
	public StatsServer getStatsValues(EmsStatNames emsStats, Date timestamp, long respTime) {

		// Select the destinations that comply with the patterns. Performed here to include destinations created while the monitoring process is running
		// Using LinkedHashSet to maintain insertion order and avoid duplicates
		final Set<DestinationInfo> queues = new LinkedHashSet<>();
		final Set<DestinationInfo> topics = new LinkedHashSet<>();
		
		queues.addAll(getDestinations(DestinationType.QUEUE));
		topics.addAll(getDestinations(DestinationType.TOPIC));
		
		log.debug("Selected queues: {}", CSVSerializer.iterableToCSV(queues));
		log.debug("Selected topics: {}", CSVSerializer.iterableToCSV(topics));
		
		StatsServer statsServer = new StatsServer(serverInfo);

		// Server stats		
		statsServer.getStats().putAll(EmsLoggerConstants.getStatsValues(emsStats.getServerStatsNames(), getServerInfo()));
		
		// Queues stats							
		if (queues != null) {
			statsServer.getQueuesStats().addAll(getDestinationsStatsValues(emsStats, queues, DestinationType.QUEUE));
		}

		// Topics stats
		if (topics != null) {
			statsServer.getTopicsStats().addAll(getDestinationsStatsValues(emsStats, topics, DestinationType.TOPIC));
		}		
		
		log.debug(
				"Collected server stats for server: {} - queue stats: {} - topics stats: {} - at: {} - response time: {}",
				getAlias(), statsServer.getQueuesStats().size(), statsServer.getTopicsStats().size(), timestamp,
				respTime);
		
		return statsServer;		
	}
	
	private static List<DestinationStats> getDestinationsStatsValues(EmsStatNames emsStatNames, Set<DestinationInfo> destinations, DestinationType destinationType) {
		List<DestinationStats> destinationsStats = new ArrayList<>();
		for (DestinationInfo destination : destinations) {
			log.info("Retrieving stats for destination: {}", destination.getName());

			DestinationStats destinationStats = new DestinationStats(destination, destinationType);

			destinationStats.getStats().putAll(
					EmsLoggerConstants.getStatsValues(emsStatNames.getQueueStatsNames(), destination));
			
			destinationStats.getStatsInbound().putAll(
					EmsLoggerConstants.getStatsValues(emsStatNames.getDestinationInboudStatsNames(), destination.getInboundStatistics()));
			
			destinationStats.getStatsOutbound().putAll(
					EmsLoggerConstants.getStatsValues(emsStatNames.getDestinationOutboundStatsNames(), destination.getOutboundStatistics()));
			
			destinationsStats.add(destinationStats);
		}
		return destinationsStats;
	}
		
	private Set<DestinationInfo> getDestinations(DestinationType destinationType) {
		Set<DestinationInfo> destinationInfoSet = new LinkedHashSet<>();
		
		if (adminConn == null) {
			return destinationInfoSet;
		}
		
		for (Entry<String, EmsDestination> entry : queuePatterns.entrySet()) {		
			EmsDestination destination = entry.getValue();
			try {

				DestinationInfo[] queueInfos = getTibcoDestinations(destination.getPattern(), destinationType, destination.getPermType(), TibjmsAdmin.DEST_CURSOR_FIRST,
						EmsLoggerConstants.destCursorSize);
				while (queueInfos != null) {
					for (DestinationInfo queueInfo : queueInfos) {
						destinationInfoSet.add(queueInfo);
					}
					queueInfos = getTibcoDestinations(destination.getPattern(), destinationType, destination.getPermType(), TibjmsAdmin.DEST_CURSOR_NEXT,
							EmsLoggerConstants.destCursorSize);
				}
			} catch (TibjmsAdminException e) {
				if (e.getMessage().endsWith("server does not support that command.")) {
					log.error("server " + alias
							+ " does not support use of getQueues with cursors, disabling queue monitoring");
					// remove queue entry
					destinationInfoSet.clear();
					break;
				}
				// Always throws "No active cursor" excep on next call, if all queues returned
				// in first call.
				// So make sure we ignore this case bug.

				if (!e.getMessage().endsWith("No active cursor")) {
					log.error("calling getQueues for server " + alias + " pattern " + destination.getPattern() + " "
							+ e.getMessage());
					try {
						// Ensure cursor is closed
						getTibcoDestinations(destination.getPattern(), destinationType, destination.getPermType(), TibjmsAdmin.DEST_CURSOR_LAST, 1);
					} catch (Exception xx) {
						// Don't do anything						
					}
				}				
			} catch (Exception e) {
				log.error(e.toString() + " disabling queue monitoring for server " + alias);
				// We can get here for old API's that have no cursor option
				destinationInfoSet.clear();
				break;
			}
			
			if (destinationInfoSet.size() >= EmsLoggerConstants.maxDestinations)
				log.warn("getQueues for server " + alias + " pattern " + destination.getPattern() + " returned many queues: "
						+ destinationInfoSet.size());
		}
		return destinationInfoSet;
	}

	private DestinationInfo[] getTibcoDestinations(String pattern, DestinationType destinationType, int permType, int cursorAction, int cursorSize) throws TibjmsAdminException {
		DestinationInfo[] destinationInfo = null;
		
		switch (destinationType) {
		case QUEUE:
			destinationInfo = adminConn.getQueues(pattern, permType, cursorAction, cursorSize);
			break;
		case TOPIC:
			destinationInfo = adminConn.getTopics(pattern, permType, cursorAction, cursorSize);
			break;
		}
		return destinationInfo;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("alias=%s - url=%s - user=%s - sslParams=%s", alias, url, user, sslParams));
		sb.append(String.format(" - queue Patterns=%s", CSVSerializer.iterableToCSV(queuePatterns.values())));
		sb.append(String.format("- topic Patterns=%s", CSVSerializer.iterableToCSV(topicPatterns.values())));
		return sb.toString();
	}
		
}
