package eps.platform.infraestructure.internal;

import lombok.Getter;

public enum NMONCpuLiterals {
	USER ("USER%"), 
	SYS ("SYS%"), 
	WAIT ("WAIT%"), 
	IDLE ("IDLE%"),
	STEAL ("STEAL%"), 
	BUSY("BUSY"),
	CPUS("CPUS"),
	PCPUS("PhysicalCPUs")
	;
	
	@Getter private final String literal;
	private NMONCpuLiterals(String literal) {
		this.literal = literal;
	}
	
    public static NMONCpuLiterals fromString(String literal) {
        for (NMONCpuLiterals b : NMONCpuLiterals.values()) {
            if (b.literal.equalsIgnoreCase(literal)) {
                return b;
            }
        }
        return null;
    }
	

}
