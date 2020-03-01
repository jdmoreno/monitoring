package eps.platform.infraestructure.json.dto.nmon;

import com.google.gson.annotations.Expose;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NetInfoDto {
	@Expose 
	@Builder.Default private Double readKB = 0.0;
	@Expose
	@Builder.Default private Double writeKB = 0.0;
	
	public Double addReadKB(Double readKB) {
		return this.readKB += readKB;
	}
	
	public Double addWriteKB(Double writeKB) {
		return this.writeKB += writeKB;
	}
	
}
