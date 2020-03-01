package eps.platform.infraestructure.ems.tibco;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import eps.platform.infraestructure.ems.stats.StatsServer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class StatsCollection {
	@Expose public final List<StatsServer> statsServers = new ArrayList<>();
}
