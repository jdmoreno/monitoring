package eps.platform.infraestructure.config;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter @Setter 
public class Server {
	
	@SerializedName("alias")
	@Expose			
	private String alias;
	

	@SerializedName("url")
	@Expose				
	private String url;
	
	@SerializedName("user")
	@Expose				
	private String user;
	
	@SerializedName("password")
	@Expose				
	private String password;

	@SerializedName("sslParams")
	@Expose				
	private List<SSLParam> sslParams;
	
	@SerializedName("queues")
	@Expose				
	private List<Queue> queues;
	
	@SerializedName("topics")
	@Expose				
	private List<Topic> topics;
	
}
