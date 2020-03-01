package eps.platform.infraestructure.internal;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@RequiredArgsConstructor 
public class DiskDetailInfo {
	@NonNull @Setter @Getter private String diskId;
	@Setter @Getter private Double diskBusy;
	@Setter @Getter private Double diskRead;
	@Setter @Getter private Double diskWrite;
	@Setter @Getter private Double diskXfer;
	@Setter @Getter private Double diskBsize;	
}
