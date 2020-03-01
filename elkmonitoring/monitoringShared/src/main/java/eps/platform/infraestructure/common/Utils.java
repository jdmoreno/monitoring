package eps.platform.infraestructure.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Utils {
	
	public static final String SEPARATOR = "-";
	public final static String[] memInfoKeys = {"memtotal", "hightotal", "lowtotal", "swaptotal", "memfree", "highfree", "lowfree", "swapfree", "memshared"
	, "cached", "active", "bigfree", "buffers", "swapcached", "inactive"};
	
	private Utils() {
	}
	
	public static String returnValueOrZero(String value) {
		if (!StringUtils.isAllEmpty(value)) {
			return value;
		} else {
			return "0";
		}		
	}
	
	public static boolean validateAccessToFile(Path filePath) {
		if (!(filePath.toFile().exists() && filePath.toFile().isFile() && Files.isReadable(filePath))) {
	    	log.error("File not found {}", filePath.toAbsolutePath());
	    	return false;
	    }
		log.info("File {} exists", filePath.toAbsolutePath());
		return true;
	}

	public static List<String> readFiletoList(String inputFilePath) throws IOException {
		return readFiletoList(Paths.get(inputFilePath));
	}
	
	public static List<String> readFiletoList(Path inputFilePath) throws IOException {
		return Files.readAllLines(inputFilePath);
	}
	
	public static String join(CharSequence delimiter,
	        Iterable<?> elements) {
	    Objects.requireNonNull(delimiter);
	    Objects.requireNonNull(elements);
	    StringJoiner joiner = new StringJoiner(delimiter);
	    for (Object cs: elements) {
	        joiner.add(cs.toString());
	    }
	    return joiner.toString();
	}
	
	public static String stack2String(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
	
	public static Path string2Path(String pathSt) {
		Path path = null;
    	try {
    		path = Paths.get(FilenameUtils.separatorsToUnix(pathSt));
    	} catch (Exception e) {
    		log.error("Argument {} is not a valid path", pathSt);
		}		
    	return path;
	}
	
}
