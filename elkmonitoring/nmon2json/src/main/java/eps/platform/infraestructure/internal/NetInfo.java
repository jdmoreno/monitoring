package eps.platform.infraestructure.internal;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.ToString;

@ToString
public class NetInfo {
	@Getter private Map <String, NetDetailInfo> netDetailInfos = new HashMap<>();

	public NetDetailInfo put(String device, String operation, double amount) {
		 NetDetailInfo netDetailInfo = netDetailInfos.get(device);
		 
		if (netDetailInfo == null) {
			netDetailInfo = new NetDetailInfo(device);
			netDetailInfos.put(device, netDetailInfo);
		}
		
		switch (operation.toUpperCase()) {
		case "READ":		
			netDetailInfo.setReadKB(amount);
			break;
			
		case "WRITE":			
			netDetailInfo.setWriteKB(amount);
			break;

		default:
			break;
		}
		return netDetailInfo;
	}
	
	public void recordNET(String[] headers, String[] tokens) {
		for (int i = 2; i < headers.length; i++) {
			if (i < tokens.length) {
				String[] headerParts = StringUtils.split(headers[i], '-');
				
				if (headerParts.length == 3) {
					double value = Double.parseDouble(tokens[i]);
					put(headerParts[0], headerParts[1], value);
				}
			}
		}
	}
}
