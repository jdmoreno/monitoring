package eps.platform.infraestructure.ems.tibco;

import eps.platform.infraestructure.config.Queue;
import eps.platform.infraestructure.config.Topic;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class EmsDestination {
	private String pattern;
	private int permType = EmsLoggerConstants.defaultDestPermType;
//	private List<DestinationInfo> destinationInfo = new ArrayList<>();
	
	public EmsDestination(Queue queue) {
		this(queue.getPattern(), EmsLoggerConstants.defaultDestPermType);
	}

	public EmsDestination(Topic topic) {
		this(topic.getPattern(), EmsLoggerConstants.defaultDestPermType);
	}
	
	public EmsDestination(String pattern, int permType) {
		this.pattern = pattern;
		this.permType = permType;
	}
}
