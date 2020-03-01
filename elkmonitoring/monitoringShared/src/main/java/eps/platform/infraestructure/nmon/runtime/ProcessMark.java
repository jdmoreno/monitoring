package eps.platform.infraestructure.nmon.runtime;

import static eps.platform.infraestructure.common.Utils.SEPARATOR;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class ProcessMark {
	
	private static final int COLUMN_WIDTH = 64;
	private static final int RECORD_WIDTH = 12;
	private static final int VALUE_WIDTH = 64;
	
	@Getter @Setter private String fileName;
	@Getter @Setter private long record;
	@Getter @Setter private String columnName;
	@Getter @Setter private String value;

	public String getHeadet() {
		String columnLit = StringUtils.center("ColumnName", COLUMN_WIDTH);
		String recordLit = StringUtils.center("Record", RECORD_WIDTH);
		String valueLit = StringUtils.center("Value", VALUE_WIDTH);
		
		return recordLit + SEPARATOR + columnLit + SEPARATOR + valueLit;		
	}
	
	public String toReport() {
		String columnLit = StringUtils.rightPad(StringUtils.abbreviate(StringUtils.defaultString(columnName), COLUMN_WIDTH, COLUMN_WIDTH), COLUMN_WIDTH);
		String recordLit = String.format("%12d", record);
		String valueLit = StringUtils.rightPad(StringUtils.abbreviate(StringUtils.defaultString(value), VALUE_WIDTH, VALUE_WIDTH), VALUE_WIDTH);
		
		return recordLit + SEPARATOR + columnLit + SEPARATOR + valueLit;
	}
	
}

