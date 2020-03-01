package eps.platform.infraestructure.internal;

import lombok.Builder;
import lombok.Getter;

@Builder
public class TopDetailInfo {
	@Getter private String command;	
	@Getter private Double cpus;	
	@Getter private String pid;
	@Getter private double cpu;
	@Getter private double usr;
	@Getter private double sys;
	@Getter private double size;
	@Getter private double resText;
	@Getter private double resData;
	
}
