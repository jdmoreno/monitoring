package eps.platform.infraestructure.json.dto.nmon;

import com.google.gson.annotations.Expose;

import lombok.Builder;
import lombok.Value;

@Value 
@Builder
public class HeaderDto {
	@Expose private String source;
	@Expose private String section;			
	@Expose private String timestamp;
	@Expose private String hostname;
	@Expose private String os;		
	@Expose private String dataCentre;
	@Expose private String environment;
	@Expose private String serverFunction;
}
