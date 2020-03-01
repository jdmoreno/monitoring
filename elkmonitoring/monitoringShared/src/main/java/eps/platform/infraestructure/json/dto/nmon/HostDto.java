package eps.platform.infraestructure.json.dto.nmon;

import com.google.gson.annotations.Expose;

import lombok.Builder;
import lombok.Value;

@Value 
@Builder
public class HostDto implements JSONDto {
	@Expose private String type = "NMON_STATS_HOST";
	
	@Expose private HeaderDto header;
	
	// CPU
	@Expose private CpuInfoDto cpuInfo;

	// MEMORY
	@Expose private MemInfoDto memInfo;
	
	// NET
	@Expose private NetInfoDto netInfo;
	
}


