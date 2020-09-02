package eps.platform.infraestructure.dto.ems;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.Expose;

import eps.platform.infraestructure.dto.nmon.HeaderDto;
import lombok.Builder;
import lombok.Value;

@Value 
@Builder
public class DestinationInfoDto {
	@Expose private String type = "EMS_STATS_DESTINATION";
	
	@Expose private HeaderDto header;
	
	// Server information
	@Expose private final String serverName;
	@Expose private final String hostname;
	
	// Destination information
	@Expose private final String destinationName;
	@Expose private final String destinationType;
	@Expose private final String store;
	@Expose private final boolean staticSw;
	@Expose private final boolean temporary;
	
	@Expose private final Map<String, Object> stats  = new HashMap<>();
	@Expose private final Map<String, Object> statsInbound = new HashMap<>();
	@Expose private final Map<String, Object> statsOutbound = new HashMap<>();
}
