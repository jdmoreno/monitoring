package eps.platform.infraestructure.ems.stats;

import java.lang.reflect.Method;

import lombok.Getter;

@Getter
public class StatName implements Comparable<StatName> {
	private String displayName;
	
	private StatName (String displayName) {
		this.displayName = displayName;
	}
	
	// Returns stats display name from ServerInfo method name
	public static StatName getStatsDisplayName(Method method) {
		String displayName = method.getName();
		if (Character.isLowerCase(displayName.charAt(4))) {
			displayName = Character.toLowerCase(displayName.charAt(3)) + displayName.substring(4);
		} else {
			displayName = displayName.substring(3);
		}
		return new StatName(displayName);
	}
	
	// Returns stats display name from ServerInfo method name
	public static StatName getStatsDisplayName(Method method, String prefix) {
		StatName statName = getStatsDisplayName(method);
		if (prefix != null && prefix.length() > 0) {
			statName.displayName = prefix + Character.toUpperCase(statName.displayName.charAt(0)) + statName.displayName.substring(1);
		}
		return statName;
	}

	@Override
	public int compareTo(StatName o) {
		return this.getDisplayName().compareTo(o.getDisplayName());
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}
