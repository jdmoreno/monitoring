package eps.platform.infraestructure.nmon.json;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import eps.platform.infraestructure.dto.nmon.CpuInfoDto;
import eps.platform.infraestructure.dto.nmon.DiskDetailInfoDto;
import eps.platform.infraestructure.dto.nmon.HeaderDto;
import eps.platform.infraestructure.dto.nmon.HostDto;
import eps.platform.infraestructure.dto.nmon.MemInfoDto;
import eps.platform.infraestructure.dto.nmon.NetDetailInfoDto;
import eps.platform.infraestructure.dto.nmon.NetInfoDto;
import eps.platform.infraestructure.dto.nmon.TopDetailInfoDto;
import eps.platform.infraestructure.internal.DiskDetailInfo;
import eps.platform.infraestructure.internal.MemInfo;
import eps.platform.infraestructure.internal.NetDetailInfo;
import eps.platform.infraestructure.internal.TopDetailInfo;
import eps.platform.infraestructure.nmon.NMONHeader;
import eps.platform.infraestructure.nmon.NMONSection;
import eps.platform.infraestructure.nmon.csv.InMemoryDB;
import eps.platform.infraestructure.nmon.csv.Reference;
import lombok.Builder;
import lombok.Getter;

public class JSONSection {
	
	private String source = "";
	private String hostname = "";
	private String os = "";
	private String dataCentre = "";
	private String environment = "";
	private String serverFunction = "";
	
	// Host
	@Getter private HostDto hostDto;
	
	// HDD
	@Getter private List<DiskDetailInfoDto> diskInfos = new ArrayList<>();
	
	// NET
	@Getter private List<NetDetailInfoDto> netInfos = new ArrayList<>();
	
	// TOP	
	@Getter private List<TopDetailInfoDto> topDetailInfos = new ArrayList<>();

	@Builder
	public JSONSection(String source, NMONHeader nmonHeader) {
		this.source = source;
		this.hostname = nmonHeader.getHostname();
		this.os = nmonHeader.getOs().name();
		
		// Enrich information from the reference CSV if the hostname exists
		Reference reference = InMemoryDB.get(nmonHeader.getHostname());		
		if (reference != null) {
			this.dataCentre = reference.getDataCentre();
			this.environment = reference.getEnvironment();
			this.serverFunction = reference.getServerFunction();
		}				
	}
	
	/**
	 * @param nmonSection
	 */
	public void generate(NMONSection nmonSection) {		
		// Clear previous sections
		diskInfos.clear();
		netInfos.clear();
		topDetailInfos.clear();
		
		// Common header
		HeaderDto header = HeaderDto.builder()
			.source(this.source)
			.section(nmonSection.getSection())
			.timestamp(nmonSection.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
			.hostname(this.hostname)
			.os(this.os)
			.dataCentre(dataCentre)
			.environment(environment)
			.serverFunction(serverFunction)
			.build();
		
		// CPU
		CpuInfoDto cpuInfoDto = CpuInfoDto.builder()
				.user(nmonSection.getUser())
				.sys(nmonSection.getSys())
				.wait(nmonSection.getWait())
				.idle(nmonSection.getIdle())
				.busy(nmonSection.getBusy())
				.steal(nmonSection.getSteal())
				.cpus(nmonSection.getCpus()).build();
		
		// MEM
		MemInfoDto memInfoDto = MemInfoDto.builder().build();
		MemInfo memInfo = nmonSection.getMemInfo();
		if (memInfo != null) {
			memInfoDto.getMap().putAll(memInfo.getMap());
		}

		// NET totals
		NetInfoDto netInfoDto = NetInfoDto.builder().build();
		hostDto = HostDto.builder().cpuInfo(cpuInfoDto).header(header).memInfo(memInfoDto).netInfo(netInfoDto).build();
		
		// HDD
		for (DiskDetailInfo diskDetailInfo : nmonSection.getDiskDetailInfos()) {
			DiskDetailInfoDto diskDetailInfoDto = DiskDetailInfoDto.builder()
					.header(header)
					.diskBsize(diskDetailInfo.getDiskBsize())
					.diskBusy(diskDetailInfo.getDiskBusy())
					.diskId(diskDetailInfo.getDiskId())
					.diskRead(diskDetailInfo.getDiskRead())
					.diskWrite(diskDetailInfo.getDiskWrite())
					.diskXfer(diskDetailInfo.getDiskXfer())
					.build();
			diskInfos.add(diskDetailInfoDto);
		}
		
		// NET
		for (NetDetailInfo netDetailInfo : nmonSection.getNetDetailInfos()) {
			if (!"LO".equalsIgnoreCase(netDetailInfo.getDevId())) {
				netInfoDto.addReadKB(netDetailInfo.getReadKB());
				netInfoDto.addWriteKB(netDetailInfo.getWriteKB());
			}
			
			NetDetailInfoDto netDetailInfoDto = NetDetailInfoDto.builder()
					.header(header)
					.deviceId(netDetailInfo.getDevId())
					.readKB(netDetailInfo.getReadKB())
					.writeKB(netDetailInfo.getWriteKB())
					.build();
			netInfos.add(netDetailInfoDto);
		}
		
		// TOP
		for (TopDetailInfo topDetailInfo : nmonSection.getTopDetailInfos()) {
			TopDetailInfoDto topDetailInfoDto = TopDetailInfoDto.builder()
					.header(header)
					.command(topDetailInfo.getCommand())
					.pid(topDetailInfo.getPid())
					.cpus(topDetailInfo.getCpus())					
					.cpu(topDetailInfo.getCpu())
					.usr(topDetailInfo.getUsr())
					.sys(topDetailInfo.getSys())
					.size(topDetailInfo.getSize())
					.resData(topDetailInfo.getResData())
					.resText(topDetailInfo.getResText())
					.build();
			topDetailInfos.add(topDetailInfoDto);
		}
	}

}
