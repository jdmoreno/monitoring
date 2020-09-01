package eps.platform.infraestructure.dto.nmon;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import eps.platform.infraestructure.common.Utils;
import lombok.Builder;
import lombok.Value;

@Value 
@Builder
public class MemInfoDto {
	private final static String[] keys = Utils.memInfoKeys;
	
	private final Map<String, Double> map = new HashMap<>();
	public Double put(String key, Double value) {
		if (Arrays.stream(keys).anyMatch(key::equals)) {
			return map.put(key, value);			
		} return null;
		
	}
	public Double get(String key) {
		return map.get(key);
	}
	
}

