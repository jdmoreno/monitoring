package eps.platform.infraestructure.json.dto.nmon;

import com.google.gson.annotations.Expose;

import lombok.Builder;
import lombok.Value;

@Value 
@Builder
public class NetDetailInfoDto implements JSONDto{
	@Expose private String type = "NMON_STATS_NET";
	
	@Expose private HeaderDto header;
	
	@Expose private String deviceId;
	@Expose private Double readKB;
	@Expose private Double writeKB;
}
