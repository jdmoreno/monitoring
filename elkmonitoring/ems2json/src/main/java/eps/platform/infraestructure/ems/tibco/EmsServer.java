package eps.platform.infraestructure.ems.tibco;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.tibco.tibjms.admin.DestinationInfo;
import com.tibco.tibjms.admin.QueueInfo;
import com.tibco.tibjms.admin.ServerInfo;
import com.tibco.tibjms.admin.TibjmsAdmin;
import com.tibco.tibjms.admin.TibjmsAdminException;
import com.tibco.tibjms.admin.TopicInfo;

import eps.platform.infraestructure.DestinationType;
import eps.platform.infraestructure.config.Queue;
import eps.platform.infraestructure.config.Server;
import eps.platform.infraestructure.config.Topic;
import eps.platform.infraestructure.ems.stats.StatsDestination;
import eps.platform.infraestructure.ems.stats.StatsServer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@ToString
public class EmsServer {
	private String alias;
	private String url;
	private String user;
	private String password;
	private Map<?,?> sslParams = null;
	
	private final Map<String, EmsDestination> queues = new HashMap<>();
	private final Map<String, EmsDestination> topics = new HashMap<>();
	
	@Setter private TibjmsAdmin adminConn = null;	
	@Setter private TibjmsAdmin staleConn = null;	
	@Setter private ServerInfo serverInfo = null;
	
	public EmsServer(Server yamlServer) {
		this(yamlServer.getAlias(), yamlServer.getUrl(), yamlServer.getUser(), yamlServer.getPassword(), null, null, null);		
		
		queues.clear();
		for (Queue queue : yamlServer.getQueues()) {
			queues.put(queue.getPattern(), new EmsDestination(queue));
		}

		topics.clear();
		for (Topic topic : yamlServer.getTopics()) {
			topics.put(topic.getPattern(), new EmsDestination(topic));
		}
	}
	
	public EmsServer(String alias, String url, String user, String password, Map<?,?> sslParams, Map<String, EmsDestination> queues, Map<String, EmsDestination> topics) {
		this.alias = alias;
		this.url = url;
		this.user = user;
		this.password = password;
		this.sslParams = sslParams;
		
		if (queues != null) {
			this.queues.putAll(queues);
		}
		
		if (topics != null) {
			this.topics.putAll(topics);
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
		log.warn("Disconnected from Server " + alias + " " + reason);
	}
	
	public StatsServer getStats(Date timestamp, long respTime) {

		StatsServer stats = new StatsServer(serverInfo);

		@SuppressWarnings("unused")
		QueueInfo[] queueInfo = getQueueStats();
		
		@SuppressWarnings("unused")
		TopicInfo[] TopicInfo = getTopicStats();
		
		// Server stats		
		stats.getStats().putAll(EmsLoggerConstants.getStatsMethodValues(getServerInfo(), null));
		
		// Queues stats							
		int qc = 0;
		if (getQueues() != null) {
			for (Entry<String, EmsDestination> emsDestinationEntry : getQueues().entrySet()) {
				EmsDestination destination = emsDestinationEntry.getValue();
				
				for (int i = 0; i < destination.destinationInfo.length; i++) {
					DestinationInfo destinationInfo = destination.destinationInfo[i];							
					StatsDestination statsDestination = new StatsDestination(destinationInfo, DestinationType.QUEUE);
					
					statsDestination.getStats().putAll(EmsLoggerConstants.getStatsMethodValues(destinationInfo, null));
					statsDestination.getStatsInbound().putAll(EmsLoggerConstants.getStatsMethodValues(destinationInfo.getInboundStatistics(), "inbound"));
					statsDestination.getStatsOutbound().putAll(EmsLoggerConstants.getStatsMethodValues(destinationInfo.getOutboundStatistics(), "outbound"));
					
					stats.getDestinations().add(statsDestination);
					qc++;
				}
				
			}
		}

		// Topics stats
		int tc = 0;		
		if (getTopics() != null) {
			for (Entry<String, EmsDestination> emsDestinationEntry : getTopics().entrySet()) {
				EmsDestination destination = emsDestinationEntry.getValue();

				for (int i = 0; i < destination.destinationInfo.length; i++) {
					DestinationInfo destinationInfo = destination.destinationInfo[i];							
					StatsDestination statsDestination = new StatsDestination(destinationInfo, DestinationType.TOPIC);
					
					statsDestination.getStats().putAll(EmsLoggerConstants.getStatsMethodValues(destination.destinationInfo[i], null));
					statsDestination.getStatsInbound().putAll(EmsLoggerConstants.getStatsMethodValues(destination.destinationInfo[i].getInboundStatistics(), "inbound"));
					statsDestination.getStatsOutbound().putAll(EmsLoggerConstants.getStatsMethodValues(destination.destinationInfo[i].getOutboundStatistics(), "outbound"));
					
					stats.getDestinations().add(statsDestination);
					tc++;
				}
			}
		}		
		
		log.debug("Collected server stats for server " + getAlias() + " queues: " + qc + " topics: "
				+ tc + " at " + timestamp + " response time(ms): " + respTime);
		
		return stats;		
	}
	
	public QueueInfo[] getQueueStats() {
		QueueInfo[] destinationInfo = null;
		
		for (Entry<String, EmsDestination> entry : queues.entrySet()) {		
			EmsDestination destination = entry.getValue();
			destination.destinationInfo = null;
			try {

				destinationInfo = adminConn.getQueues(destination.pattern, destination.permType, TibjmsAdmin.DEST_CURSOR_FIRST,
						EmsLoggerConstants.destCursorSize);
				while (destinationInfo != null) {
					destination.destinationInfo = EmsLoggerConstants.concatArrays(destination.destinationInfo, destinationInfo);
					destinationInfo = adminConn.getQueues(destination.pattern, destination.permType, TibjmsAdmin.DEST_CURSOR_NEXT,
							EmsLoggerConstants.destCursorSize);
				}
			} catch (TibjmsAdminException e) {
				if (e.getMessage().endsWith("server does not support that command.")) {
					log.error("server " + alias
							+ " does not support use of getQueues with cursors, disabling queue monitoring");
					// remove queue entry
					queues.clear();
					break;
				}
				// Always throws "No active cursor" excep on next call, if all queues returned
				// in first call.
				// So make sure we ignore this case bug.

				if (!e.getMessage().endsWith("No active cursor")) {
					log.error("calling getQueues for server " + alias + " pattern " + destination.pattern + " "
							+ e.getMessage());
					try {
						// Ensure cursor is closed
						adminConn.getQueues(destination.pattern, destination.permType, TibjmsAdmin.DEST_CURSOR_LAST, 1);
					} catch (Exception xx) {
						// Don't do anything						
					}
				}				
			} catch (Exception e) {
				log.error(e.toString() + " disabling queue monitoring for server " + alias);
				// We can get here for old API's that have no cursor option
				queues.clear();
				break;
			}
			if (destination.destinationInfo.length >= EmsLoggerConstants.maxDestinations)
				log.warn("getQueues for server " + alias + " pattern " + destination.pattern + " returned many queues: "
						+ destination.destinationInfo.length);
		}
		return destinationInfo;
	}

	public TopicInfo[] getTopicStats() {
		TopicInfo[] destinationInfo = null;
		
		for (Entry<String, EmsDestination> entry : topics.entrySet()) {
			EmsDestination destination = entry.getValue();
			destination.destinationInfo = null;
			try {

				destinationInfo = adminConn.getTopics(destination.pattern, destination.permType, TibjmsAdmin.DEST_CURSOR_FIRST,
						EmsLoggerConstants.destCursorSize);
				while (destinationInfo != null) {
					destination.destinationInfo = EmsLoggerConstants.concatArrays(destination.destinationInfo, destinationInfo);
					destinationInfo = adminConn.getTopics(destination.pattern, destination.permType, TibjmsAdmin.DEST_CURSOR_NEXT,
							EmsLoggerConstants.destCursorSize);
				}
			} catch (TibjmsAdminException e) {
				if (e.getMessage().endsWith("server does not support that command.")) {
					log.error("server " + alias
							+ " does not support use of getTopics with cursors, disabling topic monitoring");
					topics.clear();
					break;
				}
				// Always throws "No active cursor" excep on next call, if all topics returned
				// in first call.
				// So make sure we ignore this case bug.

				if (!e.getMessage().endsWith("No active cursor")) {
					log.error("calling getTopics for server " + alias + " pattern " + destination.pattern + " "
							+ e.getMessage());
					try {
						// Ensure cursor is closed
						adminConn.getTopics(destination.pattern, destination.permType, TibjmsAdmin.DEST_CURSOR_LAST, 1);
					} catch (Exception xx) {
						// Don't do anything
					}
				}
			} catch (Exception e) {
					log.error(e.toString() + " disabling topic monitoring for server " + alias);
					// We can get here for old API's that have no cursor option
					topics.clear();
					break;
			}
			if (destination.destinationInfo.length >= EmsLoggerConstants.maxDestinations) {
				log.warn("getTopics for server " + alias + " pattern " + destination.pattern + " returned many topics: "
						+ destination.destinationInfo.length);
			}
		}
		return destinationInfo;					
	}
}
