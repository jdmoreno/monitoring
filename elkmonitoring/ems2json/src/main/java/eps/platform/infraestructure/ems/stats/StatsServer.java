package eps.platform.infraestructure.ems.stats;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.tibco.tibjms.admin.ServerInfo;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class StatsServer {
	@Expose private final LocalDateTime timestamp;
	@Expose private final String name;
	@Expose private final String hostname;
	@Expose private final String url;
		
	@Expose private final Map<String, Object> stats = new HashMap<>();
	@Expose private final List<StatsDestination> destinations = new ArrayList<>();
		
	public StatsServer(ServerInfo serverInfo) {
		this.timestamp = LocalDateTime.now();
		this.name = serverInfo.getServerName();
		this.hostname = serverInfo.getServerHostname();
		this.url = serverInfo.getURL();
	}
}
