package eps.platform.infraestructure.nmon.csv;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import eps.platform.infraestructure.common.Utils;
import eps.platform.infraestructure.exception.EPSMonioringException;
import eps.platform.infraestructure.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InMemoryDB {

	/**
	 * Map with the lookups in memory
	 * Key: Hostname
	 * Value: List of the values of the hostname
	 */
	private static final Map<String, Reference> lookup = new HashMap<>();
	
	public static Reference get(String key) {
		return lookup.get(key.toUpperCase());
	}

	/**
	 * Load Input CSV file to be validated
	 * 
	 * @return
	 * @throws IOException 
	 * @throws Exception
	 */
	public static boolean loadReferenceCSV(Path filePath) throws EPSMonioringException, IOException {
		
		// Check if the file exists
	    if (!Utils.validateAccessToFile(filePath)) {
	    	throw new EPSMonioringException(String.format("Error opening file %s", filePath), ErrorCode.OPENING_REFFILE);
	    }

	    // Load the csv file. Use stream for those cases where the input file is not UTF-8
	    InputStream in = Files.newInputStream(filePath);
	    CSVParser csvInputFile = CSVParser.parse(in, StandardCharsets.UTF_8, CSVFormat.RFC4180.withFirstRecordAsHeader());
		
		String hostName = null;
		String dataCentre = null;
		String environment = null;
		String serverFunction = null;
		String component = null;
		
		try {
			for (CSVRecord csvRecord : csvInputFile) {
				hostName = csvRecord.get(CsvColumns.HOSTNAME.getColumnLiteral());
				if (!StringUtils.isAllBlank(hostName)) {
					hostName = hostName.trim().toUpperCase();
					
					dataCentre = getCSVValue(CsvColumns.DC.getColumnLiteral(), csvRecord);
					environment = getCSVValue(CsvColumns.ENV.getColumnLiteral(), csvRecord);
					serverFunction = getCSVValue(CsvColumns.SERVER_FUNCTION.getColumnLiteral(), csvRecord);
					component = getCSVValue(CsvColumns.COMPONENT.getColumnLiteral(), csvRecord);
					
					if (lookup.get(hostName) != null) {
						log.error("Dublicate hostname in reference table: {}", hostName);
					} else {
						lookup.put(hostName, new Reference(
								hostName,						
								dataCentre,
								environment,
								serverFunction,
								component));					
					}
				}
			}
		} catch (Exception e) {
			throw new EPSMonioringException("Invalid CSV Reference file " + filePath.toString(), e, ErrorCode.INVALID_REFFILE);
		}
		return true;
	}

	/**
	 * @param dataCentre
	 * @param csvRecord
	 * @return
	 */
	private static String getCSVValue(String columnLit, CSVRecord csvRecord) {
		String columnVal = csvRecord.get(columnLit);
		if (!StringUtils.isAllBlank(columnVal)) {
			columnVal = columnVal.trim().toUpperCase();
		}
		return columnVal;
	}	
	
}
