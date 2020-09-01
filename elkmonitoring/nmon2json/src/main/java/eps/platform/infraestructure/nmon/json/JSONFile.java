package eps.platform.infraestructure.nmon.json;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eps.platform.infraestructure.dto.nmon.DiskDetailInfoDto;
import eps.platform.infraestructure.dto.nmon.HostDto;
import eps.platform.infraestructure.dto.nmon.NetDetailInfoDto;
import eps.platform.infraestructure.dto.nmon.TopDetailInfoDto;
import eps.platform.infraestructure.json.ser.nmon.DiskDetailInfoDto_TypeAdapter;
import eps.platform.infraestructure.json.ser.nmon.HostDto_TypeAdapter;
import eps.platform.infraestructure.json.ser.nmon.NetDetailInfoDto_TypeAdapter;
import eps.platform.infraestructure.json.ser.nmon.TopDetailInfoDto_TypeAdapter;
import eps.platform.infraestructure.nmon.NMONFile;
import eps.platform.infraestructure.nmon.NMONSection;
import eps.platform.infraestructure.output.OutputManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JSONFile {
	
	private static Gson gson = null;
	
	private NMONFile nmonFile = null;	
	@Getter private Path outputFilePath = null;
	private boolean swAppend = false;
	
	/***
	 * Initialise the static gson interface
	 */
	static {
		GsonBuilder builder = new GsonBuilder();
		builder.excludeFieldsWithoutExposeAnnotation();
		builder.registerTypeAdapter(HostDto.class, new HostDto_TypeAdapter());
		builder.registerTypeAdapter(DiskDetailInfoDto.class, new DiskDetailInfoDto_TypeAdapter());
		builder.registerTypeAdapter(NetDetailInfoDto.class, new NetDetailInfoDto_TypeAdapter());
		builder.registerTypeAdapter(TopDetailInfoDto.class, new TopDetailInfoDto_TypeAdapter());
		JSONFile.gson = builder.create();		
	}
	
	public JSONFile(NMONFile nmonFile, Path outputFile, boolean swAppend) {
		
		this.nmonFile = nmonFile;
		this.swAppend = swAppend;
				
		// Apply substitutions				
		String outputFileTmp = outputFile.toString();
		outputFileTmp = StringUtils.replaceIgnoreCase(outputFileTmp, "[DC]", nmonFile.getDataCentre());
		outputFileTmp = StringUtils.replaceIgnoreCase(outputFileTmp, "[HN]", nmonFile.getHostname());
		outputFileTmp = StringUtils.replaceIgnoreCase(outputFileTmp, "[Y]", nmonFile.getDate().format(DateTimeFormatter.ofPattern("yyyy")));
		outputFileTmp = StringUtils.replaceIgnoreCase(outputFileTmp, "[M]", nmonFile.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM")));
		outputFileTmp = StringUtils.replaceIgnoreCase(outputFileTmp, "[D]", nmonFile.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		
		this.outputFilePath = Paths.get(outputFileTmp);
	}
	
	public void process() throws Exception {
		OutputManager.open(outputFilePath, swAppend);

		// Print the records
		JSONSection jsonSection = JSONSection.builder()
				.source(this.nmonFile.getFileName())
				.nmonHeader(this.nmonFile.getNmonHeader())
				.build();
		for (NMONSection nmonSection : this.nmonFile.getNmonSections()) {
			log.debug("Generation JSON section {}", nmonSection.toString());

			jsonSection.generate(nmonSection);
			OutputManager.writeln(outputFilePath, gson.toJson(jsonSection.getHostDto()));
			
			// DISK
			for (DiskDetailInfoDto detailInfo : jsonSection.getDiskInfos()) {
				OutputManager.writeln(outputFilePath, gson.toJson(detailInfo));
			}

			// NET
			for (NetDetailInfoDto detailInfo : jsonSection.getNetInfos()) {
				OutputManager.writeln(outputFilePath, gson.toJson(detailInfo));
			}

			// TOP
			for (TopDetailInfoDto detailInfo : jsonSection.getTopDetailInfos()) {
				
				/*
				 *  Filter watchdog entry because it has crazy numbers
				 *  {"type":"NMON_STATS_TOP","source":"epcrmg506_181101_0000","section":"T0001","timestamp":"2018-11-01T00:00:02","hostname":"EPCRMG506","os":"Linux","dataCentre":"BBP","environment":"PREPROD","serverFunction":"EMS APP","command":"WATCHDOG/0","pid":"0000006","cpus":8.0,"cpu":4.2764204331E8,"usr":7.3709426689642445E18,"sys":7.3218712144326697E18,"size":0.0,"resText":0.0,"resData":0.0}
				 */
				if (!"WATCHDOG/0".equals(detailInfo.getCommand())) {
					OutputManager.writeln(outputFilePath, gson.toJson(detailInfo));
				} else {
					log.trace("TOP Watchdog removed");
				}
			}
		}
	}
}
