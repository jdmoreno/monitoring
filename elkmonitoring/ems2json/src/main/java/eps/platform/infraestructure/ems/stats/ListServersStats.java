package eps.platform.infraestructure.ems.stats;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class ListServersStats {
	@Expose public final List<StatsServer> statsServers = new ArrayList<>();
}
