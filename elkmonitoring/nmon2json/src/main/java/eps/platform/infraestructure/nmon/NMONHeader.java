package eps.platform.infraestructure.nmon;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString
@Slf4j
@RequiredArgsConstructor
public class NMONHeader {
	@Getter private String hostname = null;
	@Getter private LocalDate date = null;
	
	private Double cpus = null;
	private Double lparNumber = null;
	
	public double getCpus() {
		
		switch (this.os) {
		case AIX:
			return this.lparNumber;

		default:
			return this.cpus;
		}
		
	}
	
	@Getter private OS os = OS.OTHER;

	@Getter private Map<String, String[]> diskHeaders = new HashMap<>();
	@Getter private String[] netHeaders;
	@Getter private String[] topHeaders;
	@Getter private String[] cpuAllHeaders;

	public void processRecord(String[] tokens) throws Exception {
		switch (tokens[0]) {
		case "AAA":
			processHeaderAAA(tokens);
			break;
			
		case "BBBP":
			break;
			
		case "CPU_ALL":
			cpuAllHeaders = tokens;
			break;
			
		case "DISKBUSY":
			
		case "DISKREAD":
			
		case "DISKWRITE":
			
		case "DISKXFER":
			
		case "DISKBSIZE":
			diskHeaders.put(tokens[0], tokens);
			break;
			
		case "NET":
			netHeaders = tokens;
			break;
			
		case "TOP":
			topHeaders = tokens;
			break;
			
		default:
			break;
		}

	}

	private void processHeaderAAA(String[] tokens) throws Exception{
		String key = tokens[1].toUpperCase();

		switch (key) {
		case "HOST":
			this.hostname = tokens[2];
			break;

		case "DATE":
			// Date format AAA,date,01-NOV-2018
			try {
				this.date = LocalDate.parse(tokens[2], NMONFile.NMON_DATE_FORMATTER);
			} catch (Exception e) {
				// If the date is invalid abort the NMON file process 
				log.error("Error parsing the NMON file date {}", tokens[2]);
				throw e;
			}
			break;

		case "CPUS":
			this.cpus = Double.parseDouble(tokens[2]); 
			break;
			
		case "LPARNUMBERNAME":
			this.lparNumber = Double.parseDouble(tokens[2]); 
			break;
			
		case "OS":
			this.os = OS.LINUX;
			break;
			
		case "BUILD":
			this.os = OS.AIX;
			break;
			
		default:
			break;
		}
	}
	
	public enum OS {
		LINUX, AIX, OTHER
	}
}
