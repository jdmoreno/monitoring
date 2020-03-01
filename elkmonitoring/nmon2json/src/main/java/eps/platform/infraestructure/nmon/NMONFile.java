package eps.platform.infraestructure.nmon;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import eps.platform.infraestructure.common.Utils;
import eps.platform.infraestructure.exception.EPSMonioringException;
import eps.platform.infraestructure.exception.ErrorCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString
@Slf4j
public class NMONFile {
	public static final DateTimeFormatter NMON_DATE_FORMATTER = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("dd-MMM-yyyy").toFormatter(Locale.ENGLISH);
	public static final DateTimeFormatter NMON_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	@Getter private Path inputFilePath = null;
	@Getter private String fileName = null;
	@Getter private String dataCentre = null;
	
	@Getter private NMONHeader nmonHeader = null;
	public LocalDate getDate() {
		return nmonHeader.getDate();
	}

	public String getHostname() {
		return nmonHeader.getHostname();
	}
	
	@Getter private final List<NMONSection> nmonSections = new ArrayList<>();
	
	// Tracks what part of the NMON file is processing
	@Getter private Sections section = null;
	
	public NMONFile(Path inputFilePath, String dataCentre) {
		this.inputFilePath = inputFilePath;
		this.fileName = inputFilePath.getFileName().toString();
		this.dataCentre = dataCentre;
	}
	
	public void process() throws Exception {
		log.info("Input file: {}", inputFilePath);

		// Clear previous execution		
		this.nmonHeader = new NMONHeader();		
		this.nmonSections.clear();
		
		// Start file with header
		this.section = Sections.HEADER;
		
		List<String> nmonInputFile = null;
		try {
			nmonInputFile = Utils.readFiletoList(inputFilePath);
		} catch (Exception e) {
			throw new EPSMonioringException("Invalid NMON file", 
					e, 
					ErrorCode.INVALID_NMON_FILE);			
		}
		
		for (String record : nmonInputFile) {
			log.debug("Record read: {}", record);
			String[] tokens = StringUtils.splitPreserveAllTokens(record, ',');
			processRecord(tokens);
		}
	}	
	
	private void processRecord(String[] tokens) throws Exception {
		// Check if the file header is finish or if there is stat section change
		if ("ZZZZ".equals(tokens[0])) {
			section = Sections.STAT;
			nmonSections.add(new NMONSection());
		}

		switch (section) {
		case HEADER:
			nmonHeader.processRecord(tokens);
			break;
		case STAT:
			// Get the last section
			NMONSection nmonSection = nmonSections.get(nmonSections.size() - 1);

			if (nmonSection != null) {
				nmonSection.processRecord(nmonHeader, tokens);
			} else {
				log.error("Stat section should not be null");
			}
			break;
		}			
	}
}
