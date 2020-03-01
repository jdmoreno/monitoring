package eps.platform.infraestructure.ems.tibco;

import java.util.ArrayList;
import java.util.List;

import com.tibco.tibjms.admin.QueueInfo;
import com.tibco.tibjms.admin.ServerInfo;
import com.tibco.tibjms.admin.StatData;
import com.tibco.tibjms.admin.TopicInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmsStats {
	protected static final  List<String> serverStatsNames = new ArrayList<>();
	protected static final List<String> queueStatsNames = new ArrayList<>();
	protected static final List<String> topicStatsNames = new ArrayList<>();

	public static void init() {
		EmsLoggerConstants.getStatsMethodNames(serverStatsNames, ServerInfo.class, null);
		log.debug("Added server stats names " + serverStatsNames);
		
		EmsLoggerConstants.getStatsMethodNames(queueStatsNames, QueueInfo.class, null);
		EmsLoggerConstants.getStatsMethodNames(queueStatsNames, StatData.class, "inbound");
		EmsLoggerConstants.getStatsMethodNames(queueStatsNames, StatData.class, "outbound");
		log.debug("Added queue stats names " + queueStatsNames);
		
		EmsLoggerConstants.getStatsMethodNames(topicStatsNames, TopicInfo.class, null);
		EmsLoggerConstants.getStatsMethodNames(topicStatsNames, StatData.class, "inbound");
		EmsLoggerConstants.getStatsMethodNames(topicStatsNames, StatData.class, "outbound");
		log.debug("Added topic stats names " + topicStatsNames);
	}
}
