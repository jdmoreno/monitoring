package eps.platform.infraestructure.dto.nmon;

import com.google.gson.annotations.Expose;

import lombok.Builder;
import lombok.Value;

@Value 
@Builder
public class DiskDetailInfoDto implements JSONDto{
	@Expose private String type = "NMON_STATS_DISK";
	
	@Expose private HeaderDto header;
	
	@Expose private String diskId;
	@Expose private Double diskBusy;
	@Expose private Double diskRead;
	@Expose private Double diskWrite;
	@Expose private Double diskXfer;
	@Expose private Double diskBsize;	
}
