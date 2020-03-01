package eps.platform.infraestructure.internal;

import eps.platform.infraestructure.common.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class CPUInfo {
	private Double user = 0.0;
	private Double sys = 0.0;
	private Double wait = 0.0;
	private Double idle = 0.0;
	private Double steal = 0.0;
	private Double busy = 0.0;
	private Double cpus = 0.0;
	
	// AIX: CPU_ALL,CPU Total epcrmg506,User%,Sys%,Wait%,Idle%,Busy,CPUs
	// Linux: CPU_ALL,CPU Total EPCRMG564,User%,Sys%,Wait%,Idle%,Steal%,Busy,CPUs	
	public void recordCpuAll(String[] cpuAllHeaders, String[] tokens) {
		for (int i = 2; i < cpuAllHeaders.length; i++) {
			if (i < tokens.length) {
				String cpuField = cpuAllHeaders[i].trim();

				NMONCpuLiterals literal = NMONCpuLiterals.fromString(cpuField);
				if (literal == null) {
					log.error("Unknown CPU ALL literal {} found in header", cpuField);
				} else {
					double value = 0;				
					value = parseDouble(tokens[i]);
					
					switch (literal) {
					case USER:
						this.setUser(value);
						break;
					case SYS:
						this.setSys(value);
						break;
					case WAIT:
						this.setWait(value);
						break;
					case IDLE:
						this.setIdle(value);
						break;
					case STEAL:
						this.setSteal(value);
						break;
					case BUSY:
						this.setBusy(value);
						break;
					case CPUS:
						this.setCpus(value);
						break;
					case PCPUS:
						break;						
					}
				}
			}
		}
	}

	private double parseDouble(String token) {
		double value = 0;
		try {					
			value = Double.parseDouble(Utils.returnValueOrZero(token));
		} catch (Exception e) {
			log.error("Parsing double {}. Omitting this value", token);
		}
		return value;
	}
}
