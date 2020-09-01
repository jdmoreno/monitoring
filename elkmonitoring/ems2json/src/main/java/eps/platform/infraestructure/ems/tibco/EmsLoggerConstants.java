package eps.platform.infraestructure.ems.tibco;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public static void getStatsMethodNames(List<String> v, Class<? extends Object> c, String prefix) {
		try {
			Method[] methods = c.getMethods();
			for (int i = 0; i < methods.length; i++) {
				String methodName = methods[i].getName();
				if (matchStatsMethod(methods[i], methodName)) {
					String displayName = getStatsDisplayName(methodName);
					if (prefix != null && prefix.length() > 0) {
						displayName = prefix + Character.toUpperCase(displayName.charAt(0)) + displayName.substring(1);
					}
					v.add(displayName);
				}
			}
		} catch (Exception ex) {
			log.error(ex.getClass() + ": " + ex.getMessage());
		}
	}
	
	public static Map<String, Object> getStatsMethodValues(Object obj, String prefix) {
		Class<? extends Object> c = obj.getClass();
		Map<String, Object> stats = new HashMap<>(); 

		Method[] methods = c.getMethods();
		for (int i = 0; i < methods.length; i++) {
			String methodName = methods[i].getName();
			if (matchStatsMethod(methods[i], methodName)) {
				try {
					String displayName = getStatsDisplayName(methodName);
					if (prefix != null && prefix.length() > 0) {
						displayName = prefix + Character.toUpperCase(displayName.charAt(0)) + displayName.substring(1);
					}
					Object ret = methods[i].invoke(obj, (java.lang.Object[]) null);
					stats.put(displayName, ret);
				} catch (Exception ex) {
					log.error(ex.getClass() + ": " + ex.getMessage());
				}
			}
		}
		return stats;
	}
	
	// Returns stats display name from ServerInfo method name
	public static String getStatsDisplayName(String methodName) {

		String displayName;
		if (Character.isLowerCase(methodName.charAt(4)))
			displayName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
		else
			displayName = methodName.substring(3);
		return displayName;
	}
	
	// Returns if ServerInfo method returns a stats value that should be logged.
	private static boolean matchStatsMethod(Method method, String methodName) {
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
	
	public static <T> T[] concatArrays(T[] first, T[] second) {
		if (first == null)
			return second;
	
		if (second == null)
			return first;
	
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

}
