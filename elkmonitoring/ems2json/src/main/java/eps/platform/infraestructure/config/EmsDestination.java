package eps.platform.infraestructure.config;

import eps.platform.infraestructure.common.Common;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class EmsDestination {
	private String pattern;
	private int permType = Common.defaultDestPermType;
//	private List<DestinationInfo> destinationInfo = new ArrayList<>();
	
	public EmsDestination(Queue queue) {
		this(queue.getPattern(), Common.defaultDestPermType);
	}

	public EmsDestination(Topic topic) {
		this(topic.getPattern(), Common.defaultDestPermType);
	}
	
	public EmsDestination(String pattern, int permType) {
		this.pattern = pattern;
		this.permType = permType;
	}
}
