package eps.platform.infraestructure.nmon;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import lombok.Getter;
import lombok.ToString;

@ToString
public class RecordZZZZ {
	@Getter private String section = null;
	@Getter private LocalDateTime timestamp;
	
	public RecordZZZZ(String[] tokens) throws DateTimeParseException {		
		this.section = tokens[1];
		
		LocalTime localTime = LocalTime.parse(tokens[2], NMONFile.NMON_TIME_FORMATTER);
		LocalDate localDate = LocalDate.parse(tokens[3], NMONFile.NMON_DATE_FORMATTER);
		
		this.timestamp = LocalDateTime.of(localDate, localTime);
	}
}
