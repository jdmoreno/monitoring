package eps.platform.infraestructure.nmon;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import eps.platform.infraestructure.internal.CPUInfo;
import eps.platform.infraestructure.internal.DiskDetailInfo;
import eps.platform.infraestructure.internal.DiskInfo;
import eps.platform.infraestructure.internal.MemInfo;
import eps.platform.infraestructure.internal.NetDetailInfo;
import eps.platform.infraestructure.internal.NetInfo;
import eps.platform.infraestructure.internal.TopDetailInfo;
import eps.platform.infraestructure.internal.TopInfo;
import lombok.Getter;
import lombok.ToString;

@ToString
public class NMONSection {
	
	@Getter
	private RecordZZZZ recordZZZZ = null;
	
	public String getSection() {
		return recordZZZZ.getSection();
	}

	@Getter
	private DiskInfo diskInfo = null;
	
	public List<DiskDetailInfo> getDiskDetailInfos() {
		List<DiskDetailInfo> diskDetailsInfos = new ArrayList<>();
		if (diskInfo != null && diskInfo.getDiskDetailInfos() != null) {
			SortedSet<String> keys = new TreeSet<>(diskInfo.getDiskDetailInfos().keySet());
			for (String key : keys) {
				diskDetailsInfos.add(diskInfo.getDiskDetailInfos().get(key));
			}
		}
		return diskDetailsInfos;
	}

	@Getter
	private NetInfo netInfo = null;
	
	public List<NetDetailInfo> getNetDetailInfos() {
		List<NetDetailInfo> netDetailsInfos = new ArrayList<>();
		if (netInfo.getNetDetailInfos() != null) {
			SortedSet<String> keys = new TreeSet<>(netInfo.getNetDetailInfos().keySet());
			for (String key : keys) {
				netDetailsInfos.add(netInfo.getNetDetailInfos().get(key));
			}
		}
		return netDetailsInfos;
	}

	@Getter
	private CPUInfo cpuInfo = null;

	@Getter
	private MemInfo memInfo = null;

	@Getter
	private TopInfo topInfo = null;
	public List<TopDetailInfo> getTopDetailInfos() {
		return topInfo.getTopDetailInfos();
	}
	
	public void processRecord(NMONHeader nmonHeader, String[] tokens) {
		switch (tokens[0]) {
		case "ZZZZ":
			recordZZZZ = processZZZZ(tokens);
			diskInfo = new DiskInfo();
			cpuInfo = new CPUInfo();
			memInfo = new MemInfo();
			netInfo = new NetInfo();
			topInfo = new TopInfo(); 			
			break;

		case "MEM":
			processMEM(tokens);
			break;

		case "VM":
			break;

		case "PROC":
			break;

		case "NET":
			processNET(nmonHeader.getNetHeaders(), tokens);
			break;

		case "NETPACKET":
			break;

		case "JFSFILE":
			break;

		case "DISKBUSY":
			procesDISKBUSY(nmonHeader.getDiskHeaders().get(tokens[0]), tokens);
			break;

		case "DISKREAD":
			procesDISKREAD(nmonHeader.getDiskHeaders().get(tokens[0]), tokens);
			break;

		case "DISKWRITE":
			procesDISKWRITE(nmonHeader.getDiskHeaders().get(tokens[0]), tokens);
			break;

		case "DISKXFER":
			procesDISKXFER(nmonHeader.getDiskHeaders().get(tokens[0]), tokens);
			break;

		case "DISKBSIZE":
			procesDISKBSIZE(nmonHeader.getDiskHeaders().get(tokens[0]), tokens);
			break;

		case "TOP":
			procesTOP(nmonHeader.getTopHeaders(), tokens, nmonHeader.getCpus());
			break;

		case "CPU_ALL":
			processCpuAll(nmonHeader.getCpuAllHeaders(), tokens);
			break;

		default:
			break;
		}
	}

	private RecordZZZZ processZZZZ(String[] tokens) {
		return new RecordZZZZ(tokens);
	}

	private void processMEM(String[] tokens) {
		memInfo.recordMEM(tokens);
	}

	private void processNET(String[] headers, String[] tokens) {
		netInfo.recordNET(headers, tokens);
	}
	
	private void procesDISKBUSY(String[] headers, String[] tokens) {
		diskInfo.recordDISKBUSY(headers, tokens);
	}

	private void procesDISKREAD(String[] headers, String[] tokens) {
		diskInfo.recordDISKREAD(headers, tokens);
	}

	private void procesDISKWRITE(String[] headers, String[] tokens) {
		diskInfo.recordDISKWRITE(headers, tokens);
	}

	private void procesDISKXFER(String[] headers, String[] tokens) {
		diskInfo.recordDISKXFER(headers, tokens);
	}

	private void procesDISKBSIZE(String[] headers, String[] tokens) {
		diskInfo.recordDISKBSIZE(headers, tokens);
	}

	private void procesTOP(String[] headers, String[] tokens, Double cpus) {
		topInfo.recordTOP(headers, tokens, cpus);
	}

	private void processCpuAll(String[] headers, String[] tokens) {
		cpuInfo.recordCpuAll(headers, tokens);
	}

	//----	
	public LocalDateTime getTimestamp() {		
		return recordZZZZ.getTimestamp();
	}

	public double getUser() {		
		return cpuInfo.getUser();
	}

	public double getSys() {
		return cpuInfo.getSys();
	}

	public double getWait() {
		return cpuInfo.getWait();
	}

	public double getIdle() {
		return cpuInfo.getIdle();
	}

	public double getBusy() {
		return cpuInfo.getBusy();
	}

	public double getSteal() {
		return cpuInfo.getSteal();
	}
	
	public double getCpus() {
		return cpuInfo.getCpus();
	}
}
