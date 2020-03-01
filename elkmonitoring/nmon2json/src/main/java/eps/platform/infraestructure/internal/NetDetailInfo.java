package eps.platform.infraestructure.internal;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class NetDetailInfo {
	@NonNull @Getter private String devId;
	@Setter @Getter private Double readKB;
	@Setter @Getter private Double writeKB;
}
