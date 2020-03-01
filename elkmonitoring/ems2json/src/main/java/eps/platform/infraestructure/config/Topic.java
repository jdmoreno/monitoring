package eps.platform.infraestructure.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter @Setter 
public class Topic {

	@SerializedName("pattern")
	@Expose		
	private String pattern;
	
//	@SerializedName("logDir")
//	@Expose		
//	private String logDir;
//	
//	@SerializedName("logFile")
//	@Expose		
//	private String logFile;
}
