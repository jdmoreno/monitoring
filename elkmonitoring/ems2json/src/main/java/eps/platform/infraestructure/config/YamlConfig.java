package eps.platform.infraestructure.config;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class YamlConfig {
	@SerializedName("servers")
	@Expose	
	@Getter @Setter private List<Server> servers;	
}
