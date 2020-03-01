package eps.platform.infraestructure.nmon.csv;

import lombok.Getter;

/***
 * Column names of the Infraesturcture spreadsheet
 * 
 * @author jdmoreno
 *
 */
public enum CsvColumns {
	HOSTNAME ("HOSTNAME"), 
	IP_ADDRESS ("IP ADDRESS"), 
	VCPU ("VCPU"), 
	RAM ("RAM [GB]"),
	DISK_LOCAL ("DISK [GB] - LOCAL"), 
	STORAGE_TIER("STORAGE TIER"),
	DC("DC"), 
	ENV("ENV"), 
	SERVER_FUNCTION("SERVER FUNCTION"), 
	COMPONENT("COMPONENT");
	
	@Getter private final String columnLiteral;
	private CsvColumns(String columnLiteral) {
		this.columnLiteral = columnLiteral;
	}
}
