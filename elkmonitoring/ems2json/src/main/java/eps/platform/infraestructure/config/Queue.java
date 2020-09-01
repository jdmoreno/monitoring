package eps.platform.infraestructure.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter @Setter 
public class Queue {
	
	@SerializedName("pattern")
	@Expose	
	private String pattern;

	@SerializedName("type")
	@Expose	
	private String type;
}
