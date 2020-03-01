package eps.platform.infraestructure.output;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import eps.platform.infraestructure.exception.ErrorCode;
import eps.platform.infraestructure.exception.EPSMonioringException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)	
public class OutputManager {
	private static final Map<Path, PrintWriter> pws = new HashMap<>();
	
	public static void open(Path outputFilePath, boolean swAppend) throws EPSMonioringException {
		// Manage printwritters
		PrintWriter pw = pws.get(outputFilePath);
		
		// If outputFilePath has not been used before, creare a PrintWriter and store it for later used
		// If the append argument does not exists delete the file before writting to it. Do this only once
		if (pw == null) {
			try {
			    Files.createDirectories(outputFilePath.getParent());
			} catch(FileAlreadyExistsException e){
			    // the directory already exists.
			} catch (IOException e) {
		    	log.error("Error creating output directory {}", outputFilePath.toString());
				throw new EPSMonioringException(String.format("Error creating output directory %s", outputFilePath.toString()), e, ErrorCode.OPENING_OUTPUTFILE);
			}
		
			// Truncate the file if the append argument does not exists
			OpenOption[] openOptions = {CREATE, TRUNCATE_EXISTING, WRITE};
			if (swAppend) {
				openOptions[1] = APPEND;
			}
			
			try {
				pw = new PrintWriter(Files.newBufferedWriter(outputFilePath, StandardCharsets.UTF_8, openOptions), true);
				pws.put(outputFilePath, pw);
				log.info("Writing to file JSON file {}", outputFilePath.toString());
		    } catch (IOException e) {
		    	log.error("Error opening JSON file {}, exception {}", outputFilePath.toString(), e.getClass().getName());
				throw new EPSMonioringException(String.format("Error opening JSON file %s", outputFilePath.toString()), e, ErrorCode.OPENING_OUTPUTFILE);
		    }
		}		
	}
	
	public static void writeln(Path outputFilePath, String recordJson) throws EPSMonioringException {
		PrintWriter pw = pws.get(outputFilePath);
		
		if (pw == null) {
	    	log.error("Writing JSON file. File {} not open", outputFilePath.toString());
			throw new EPSMonioringException(ErrorCode.WRITTING_OUTPUTFILE);			
		} else {
			pw.println(recordJson);
		}
	}
	
	public static void close(Path outputFilePath) throws EPSMonioringException {
		PrintWriter pw = pws.get(outputFilePath);
		
		if (pw == null) {
	    	log.error("Closing JSON file. File {} not open", outputFilePath.toString());
			throw new EPSMonioringException(ErrorCode.CLOSING_OUTPUTFILE);			
		} else {
			pw.close();
		}
		
	}
	
	/***
	 * Close the print writers
	 */	
	public static void closeAll() {
		for (PrintWriter pw : pws.values()) {
			pw.close();
		}		
	}
}
