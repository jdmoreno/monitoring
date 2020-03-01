package eps.platform.infraestructure.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import eps.platform.infraestructure.common.Utils;
import lombok.Getter;
import lombok.ToString;

@ToString
public class TopInfo {
	
	// Top info agreegated by Command. Command location in Linux and Aix is different
	@Getter List<TopDetailInfo> topDetailInfos = new ArrayList<>();

	public void recordTOP(String[] headers, String[] tokens, double cpus) {	
		
		Map<String, String> topCommand = new HashMap<>();
		for (int i = 1; i < headers.length; i++) {
			if (i < tokens.length) {
				String key = StringUtils.strip(headers[i]);
				String value = StringUtils.strip(tokens[i]).toUpperCase();
				topCommand.put(key, value);
			}
		}
		
		TopDetailInfo topDetailInfo = TopDetailInfo.builder()
			.command(topCommand.get("COMMAND"))
			.pid(topCommand.get("+PID"))
			.cpus(cpus)
			.cpu(Double.parseDouble(Utils.returnValueOrZero(topCommand.get("%CPU"))))
			.usr(Double.parseDouble(Utils.returnValueOrZero(topCommand.get("%USR"))))
			.sys(Double.parseDouble(Utils.returnValueOrZero(topCommand.get("%SYS"))))
			.size(Double.parseDouble(Utils.returnValueOrZero(topCommand.get("SIZE"))))
			.resText(Double.parseDouble(Utils.returnValueOrZero(topCommand.get("RESTEXT"))))
			.resData(Double.parseDouble(Utils.returnValueOrZero(topCommand.get("RESDATA"))))
			.build();
		
		topDetailInfos.add(topDetailInfo);
	}
}
