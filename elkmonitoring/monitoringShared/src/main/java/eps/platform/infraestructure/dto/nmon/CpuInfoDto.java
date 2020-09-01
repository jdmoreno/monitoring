package eps.platform.infraestructure.dto.nmon;

import com.google.gson.annotations.Expose;

import lombok.Builder;
import lombok.Value;

@Value 
@Builder
public class CpuInfoDto {
	@Expose private Double user;
	@Expose private Double sys;
	@Expose private Double wait;
	@Expose private Double idle;	
	@Expose private Double busy;
	@Expose private Double steal;
	@Expose private Double cpus;
}
