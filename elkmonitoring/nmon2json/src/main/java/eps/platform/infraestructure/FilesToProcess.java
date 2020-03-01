package eps.platform.infraestructure;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import lombok.Builder;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString
@Slf4j
@Builder
public class FilesToProcess {	
	private List<String> hostNamesWL;
	private List<LocalDate> datesWL;
	
	/***
	 * Generate the Map of input NMON files and output JSON files to process
	 * 
	 * @return Map with the FileNameComponents of the input NMON file and the Path to the output JSON file
	 * @throws Exception
	 */
	public List<FileNameComponents> find(Path inputPath, String dataCentre) throws Exception {
		final List<FileNameComponents> nmons = new ArrayList<>();
		
		if (inputPath.toFile().isFile()) {
			// If the input path is a file do not check other argumentsand process the file
			FileNameComponents fileNameComponents = FileNameComponents.from(inputPath);

			log.info(StringUtils.repeat('-', 80));
			log.info("Processing NMON file as argument {}", inputPath);
			nmons.add(fileNameComponents);
			
		} else {
			// Get files list. All the files with .nomn extension
			List<Path> list = null;
			try (Stream<Path> stream = Files.walk(inputPath)) {
				list = stream
						.filter(s -> s.toFile().isFile() && s.toString().endsWith(".nmon"))
						.collect(Collectors.toList());			
		    }

			if (list.isEmpty()) {
				log.warn("No NMON files to process");
			}

			for (Path path : list) {
				checkElegibility(dataCentre, nmons, path);
			}			
		}
		
		return nmons;
	}

	private void checkElegibility(String dataCentre, final List<FileNameComponents> nmons, Path path) {
		log.trace(StringUtils.repeat('-', 80));
		log.trace("Checking file elegibility {}", path);
		
		FileNameComponents fileNameComponents = FileNameComponents.from(path);

		boolean swAdd = true;
		// Check if the host is in the hostnames whitelist
		// swAdd here is not needed here but I have it to avoid problems copying pasting conditions
		if (swAdd && hostNamesWL != null) {
			swAdd = (Collections.binarySearch(hostNamesWL, fileNameComponents.getHostName()) >= 0);
			if (!swAdd) {
				log.trace("File name hostname {} not found in hostnames list", fileNameComponents.getHostName());
			}
		}

		// Check if the date is in the dates whitelist
		if (swAdd && (datesWL != null && fileNameComponents.getFileDate() != null)) {
			swAdd = (Collections.binarySearch(datesWL, fileNameComponents.getFileDate()) >= 0);
			if (!swAdd) {
				log.trace("File name date {} not found in dates list", fileNameComponents.getFileDate());
			}
		}

		// Check the datacentre
		if (swAdd && StringUtils.isNotEmpty(dataCentre)) {
			swAdd = (dataCentre.equals(fileNameComponents.getDataCentre()));
			if (!swAdd) {
				log.trace("Datecentre {} not equal to argument datacentre {}", fileNameComponents.getDataCentre(), dataCentre);
			}
		}

		if (swAdd) {
			log.info(StringUtils.repeat('-', 80));
			log.info("NMON file elegible {}", path);
			nmons.add(fileNameComponents);
		} else {
			log.trace("File not elegible {}", path);
		}
	}
}
