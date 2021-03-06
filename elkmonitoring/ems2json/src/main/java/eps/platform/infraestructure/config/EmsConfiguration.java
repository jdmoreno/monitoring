package eps.platform.infraestructure.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmsConfiguration {
	@Getter private final Map<String, EmsServer> servers = new HashMap<>();
	
	private EmsConfiguration() {
	}
	
	public static EmsConfiguration configure(List<Server> yamlServers) {
		EmsConfiguration emsConfiguration = new EmsConfiguration(); 
		for (Server yamlServer : yamlServers) {
			emsConfiguration.addServer(yamlServer);
		}
		return emsConfiguration;
	}
	
	private void addServer(Server yamlServer) {
		
		if (!servers.containsKey(yamlServer.getAlias())) {
			if (yamlServer.getUrl().startsWith("ssl") && (yamlServer.getSslParams() == null || yamlServer.getSslParams().size() == 0)) {
				log.warn("ssl URL specfied but no SSL Parameters found for Server with alias " + yamlServer.getAlias());
			} else if (!yamlServer.getUrl().startsWith("ssl") && (yamlServer.getSslParams() != null && yamlServer.getSslParams().size() > 0)) {
				log.warn("SSL Parameters found but ssl URL not specfied for Server with alias " + yamlServer.getAlias());
			}

			EmsServer emsServer = new EmsServer(yamlServer);
//			emsServer.setDestinations();
			servers.put(yamlServer.getAlias(), emsServer);
			log.debug("Added " + yamlServer.getAlias() + " " + yamlServer.getUrl() + " " + yamlServer.getUser() + " " + " queue monitors " + (yamlServer.getQueues() == null ? 0 : yamlServer.getQueues().size()) + " topic monitors "
					+ (yamlServer.getTopics() == null ? 0 : yamlServer.getTopics().size()));			
		} else {
			log.warn("EMS Server with alias " + yamlServer.getAlias() + " already exists, ignoring server at: " + yamlServer.getUrl());
		}
	}
	
}
