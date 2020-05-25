package eps.platform.infraestructure;

import lombok.Getter;

public enum DestinationType {
	QUEUE (0),
	TOPIC (1);
	
    @Getter private final int destinationCode;

    private DestinationType(int destinationCode) {
        this.destinationCode = destinationCode;
    }
	
}
