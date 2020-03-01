package eps.platform.infraestructure;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
public class FileNameComponents {
	static final DateTimeFormatter nmonFileDateFormatter = DateTimeFormatter.ofPattern("yyMMdd");
	
	@Getter
	private Path inputFile = null;
	
	@Getter
	private String hostName = null;
	
	@Getter
	private LocalDate fileDate = null;
	
	@Getter
	private String dataCentre = null;
	
	private FileNameComponents() {
	}
	
	public static FileNameComponents from(Path path) {
		FileNameComponents fileNameComponents = new FileNameComponents();
		
		fileNameComponents.inputFile = path;
		
		String pathElement;

		int pathCounter = 0;
		Path parentPath = path;

		// Parsing file path to get DataCentre. Parsing file name to get host name and date 
	    while (parentPath != null && (parentPath.getFileName() != null) && pathCounter < 4) {
	        pathElement = parentPath.getFileName().toString();

	        switch (pathCounter) {
	        case 0:
	        	// Remove the extension
	        	String fileName = pathElement.substring(0, pathElement.lastIndexOf('.'));	            
	            String[] fileNameParts = fileName.split("_");
	            
	            fileNameComponents.hostName = fileNameParts[0].toUpperCase();
	            
	            try {
	            	fileNameComponents.fileDate = LocalDate.parse(fileNameParts[1], nmonFileDateFormatter);
	            } catch (Exception e) {
	            	log.error("File name does not contain a valid date as {}", nmonFileDateFormatter.toString());
				} 
	            
	            break;
	        case 1:
	            break;
	        case 2:
	            break;
	        case 3:
	        	fileNameComponents.dataCentre = pathElement.toUpperCase();
	            break;
	        default:
	            break;
	        }
	        pathCounter++;
	        parentPath = parentPath.getParent();
	    }
	    
	    return fileNameComponents;
	}
}
