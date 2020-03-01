package eps.platform.infraestructure.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter @Setter 
public class SSLParam {
	@SerializedName("name")
	@Expose			
	private String name;
	

	@SerializedName("type")
	@Expose				
	private String type;
	
	@SerializedName("value")
	@Expose				
	private String value;

}
