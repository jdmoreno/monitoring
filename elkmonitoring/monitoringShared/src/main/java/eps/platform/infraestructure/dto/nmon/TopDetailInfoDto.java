package eps.platform.infraestructure.dto.nmon;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Builder;
import lombok.Value;

@Value 
@Builder
public class TopDetailInfoDto implements JSONDto {
	@Expose final private String type = "NMON_STATS_TOP";
	
	@Expose private HeaderDto header;
		
	
	@Expose @SerializedName("top.command") private String command;
	@Expose @SerializedName("top.pid") private String pid;
	@Expose @SerializedName("top.cpus") private Double cpus;	
	@Expose @SerializedName("top.cpu%") private Double cpu;
	@Expose @SerializedName("top.usr%") private Double usr;
	@Expose @SerializedName("top.sys%") private Double sys;
	@Expose @SerializedName("top.size") private Double size;
//	@Expose @SerializedName("top.resSet") private double resSet = 0;
	@Expose @SerializedName("top.resText") private Double resText;
	@Expose @SerializedName("top.resData") private Double resData;
//	@Expose @SerializedName("top.shdLib") private double shdLib = 0;
//	@Expose @SerializedName("top.minorFault") private double minorFault = 0;
//	@Expose @SerializedName("top.majorFault") private double majorFault = 0;
	
}
