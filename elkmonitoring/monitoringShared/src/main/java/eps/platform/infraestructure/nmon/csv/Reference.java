package eps.platform.infraestructure.nmon.csv;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Reference {
	private String hostname;
	private String dataCentre;
	private String environment;
	private String serverFunction;
	private String component;
}
