package eps.platform.infraestructure.ems.tibco;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.tibco.tibjms.admin.DestinationInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmsLoggerConstants {
	
	private EmsLoggerConstants() {
	}

	public static final String[] methodExclusionList = { "getRouteRecoverCount", "getLargeDestCount" };
	public static final String defaultURL = "tcp://localhost:7222";
	public static final String defaultUser = "admin";
	public static final String defaultPasswd = null;
	public static final int defaultDestPermType = DestinationInfo.DEST_GET_STATIC;
	public static final int minInterval = 20;
	public static final int destCursorSize = 100;
	public static final int maxDestinations = 500;

	public static String stripSpaces(String source) {
		return source.replaceAll("\\s+", "");
	}
	
	public static Map<StatName, Method> getStatsMethods(Class<? extends Object> c, String prefix) {
		Map<StatName, Method> statsMethods = new TreeMap<>();
		
		Method[] methods = c.getMethods();
		for (Method method : methods) {
			if (matchStatsMethod(method)) {
				StatName statName = StatName.getStatsDisplayName(method, prefix);
				statsMethods.put(statName, method);
			}				
		}
		return statsMethods;
	}
	
	public static Map<StatName, Object> getStatsValues(Map<StatName, Method> statsMethods, Object obj) {
		Map<StatName, Object> statsValues = new TreeMap<>(); 
		
		for (Entry<StatName, Method> statsMethod : statsMethods.entrySet()) {
			Object ret = null;
			try {
				ret = statsMethod.getValue().invoke(obj, (java.lang.Object[]) null);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				log.error(e.getClass() + ": " + e.getMessage());
			}
			statsValues.put(statsMethod.getKey(), ret);
		}
		return statsValues;
	}
	
	// Returns if ServerInfo method returns a stats value that should be logged.
	private static boolean matchStatsMethod(Method method) {		
		String methodName = method.getName();
		if (methodName.startsWith("get")) {
			Class<?>[] parameterTypes = method.getParameterTypes();
			if (parameterTypes.length > 0) {
				return false;
			}

			int modifiers = method.getModifiers();
			if (!Modifier.isTransient(modifiers)) {
				if (method.getReturnType().getName().equals("long") || method.getReturnType().getName().equals("int")) {
					if ((methodName.contains("MsgMem") || methodName.endsWith("Size") || methodName.endsWith("Count")
							|| methodName.endsWith("Rate") || methodName.endsWith("TotalMessages")
							|| methodName.endsWith("TotalBytes")) && (!methodName.contains("Log"))
							&& (!methodName.contains("Trace")) && (!methodName.contains("SSL"))
							&& (!methodName.contains("Max")) && (!methodName.contains("MessagePool"))) {
						// Check exclusion list
						for (int i = 0; i < methodExclusionList.length; i++) {
							if (methodName.endsWith(methodExclusionList[i])) {
								return false;
							}
						}
						return true;
					}
				}
			}
		}
		return false;
	}
}
