package eps.platform.infraestructure.dto.ems;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.Expose;

import eps.platform.infraestructure.dto.nmon.HeaderDto;
import lombok.Builder;
import lombok.Value;

@Value 
@Builder
public class ServerInfoDto {
	@Expose private String type = "EMS_STATS_SERVER";
	
	@Expose private final HeaderDto header;
	
	// Server information
	@Expose private final String serverName;
	@Expose private final String hostname;
	@Expose private final String url;

	@Expose private final Map<String, Object> stats = new HashMap<>();
	
}
