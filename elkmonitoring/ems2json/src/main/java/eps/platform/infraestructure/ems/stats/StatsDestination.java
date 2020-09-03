package eps.platform.infraestructure.ems.stats;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.tibco.tibjms.admin.DestinationInfo;

import eps.platform.infraestructure.DestinationType;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class StatsDestination {

	@Expose private final String name;
	@Expose private final DestinationType destinationType;
	@Expose private final String store;
	@Expose private final boolean staticSw;
	@Expose private final boolean temporary;
	
	@Expose private final Map<StatName, Object> stats  = new HashMap<>();
	@Expose private final Map<StatName, Object> statsInbound = new HashMap<>();
	@Expose private final Map<StatName, Object> statsOutbound = new HashMap<>();
	
	public StatsDestination(DestinationInfo destinationInfo, DestinationType destinationType) {
		this.name = destinationInfo.getName();
		this.destinationType = destinationType;
		this.store = destinationInfo.getStore();
		this.staticSw = destinationInfo.isStatic();
		this.temporary = destinationInfo.isTemporary();
	}
}
