package eps.platform.infraestructure.internal;

import java.util.HashMap;
import java.util.Map;

import eps.platform.infraestructure.common.Utils;
import lombok.Getter;
import lombok.ToString;

@ToString
public class MemInfo {
	private static final String[] keys = Utils.memInfoKeys;
 
	@Getter
	private final Map<String, Double> map = new HashMap<>();
	public void recordMEM(String[] tokens) {
		for(int i = 2; i < tokens.length; i++) {
			
			if (i-2 < keys.length) {
				String key = keys[i-2];
				Double value = Double.parseDouble(Utils.returnValueOrZero(tokens[i]));				
				map.put(key, value);
			}
		}
	}
}
