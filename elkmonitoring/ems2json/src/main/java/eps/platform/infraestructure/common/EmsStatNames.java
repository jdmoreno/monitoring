package eps.platform.infraestructure.common;

import java.lang.reflect.Method;
import java.util.Map;

import com.tibco.tibjms.admin.QueueInfo;
import com.tibco.tibjms.admin.ServerInfo;
import com.tibco.tibjms.admin.StatData;
import com.tibco.tibjms.admin.TopicInfo;

import eps.platform.infraestructure.ems.stats.StatName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class EmsStatNames {	
	@Getter private Map<StatName, Method> serverStatsNames;
	@Getter private Map<StatName, Method> queueStatsNames;
	@Getter private Map<StatName, Method> topicStatsNames;
	@Getter private Map<StatName, Method> destinationInboudStatsNames;
	@Getter private Map<StatName, Method> destinationOutboundStatsNames;

	public static EmsStatNames getStatsMethodNames() {
		EmsStatNames emsStatsNames = new EmsStatNames();
		
		// Get Server stats names
		emsStatsNames.serverStatsNames = Common.getStatsMethods(ServerInfo.class, null);
		log.debug("Added server stats names: " + CSVSerializer.iterableToCSV(emsStatsNames.serverStatsNames.keySet()));
		
		// Get Queue stats names
		emsStatsNames.queueStatsNames = Common.getStatsMethods(QueueInfo.class, null);
		log.debug("Added queue stats names: " + CSVSerializer.iterableToCSV(emsStatsNames.queueStatsNames.keySet()));

		// Get Topic stats names
		emsStatsNames.topicStatsNames = Common.getStatsMethods(TopicInfo.class, null);
		log.debug("Added topics stats names: " + CSVSerializer.iterableToCSV(emsStatsNames.topicStatsNames.keySet()));
		
		// Get Destination Inbound stats names
		emsStatsNames.destinationInboudStatsNames = Common.getStatsMethods(StatData.class, "inbound");
		log.debug("Added destination inbound stats names: " + CSVSerializer.iterableToCSV(emsStatsNames.destinationInboudStatsNames.keySet()));
		
		// Get Destination Outbound stats names
		emsStatsNames.destinationOutboundStatsNames = Common.getStatsMethods(StatData.class, "outbound");
		log.debug("Added destination outbound stats names: " + CSVSerializer.iterableToCSV(emsStatsNames.destinationOutboundStatsNames.keySet()));

		
		return emsStatsNames;
	}
}
