package nmon2json;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import eps.platform.infraestructure.nmon.NMONFile;
import eps.platform.infraestructure.nmon.json.JSONFile;

public class JSONFileTest {
	static NMONFile nmonFile = null;
	static JSONFile jsonFile = null;
	
	static Path inputFilePath = Paths.get("./src/test/resources/99.NMON/BBP/2018-11/2018-11-01/epcrmg506_181101_0000.nmon");
	static Path outputFilePath = Paths.get("./src/test/resources/99.JSON/[DC]/[Y]/[M]/[D]/[HN]_[D].json");	
	static Path expectedOutputFilePath = Paths.get("./src/test/resources/99.JSON/BBP/2018/2018-11/2018-11-01/epcrmg506_2018-11-01.json");
	
	@BeforeAll 
    public static void initialize() throws Exception {
		Files.deleteIfExists(expectedOutputFilePath);
		
    	nmonFile = new NMONFile(inputFilePath, "BBP");
    	nmonFile.process();
    	
    	jsonFile = new JSONFile(nmonFile, Paths.get("./src/test/resources/99.JSON/[DC]/[Y]/[M]/[D]/[HN]_[D].json"), false);
    	jsonFile.process();
     }

	/***
	 * Invalid argument l
	 */    
    @Test
    public void outputPathTest() {
    	assertThat(jsonFile.getOutputFilePath()).isEqualTo(expectedOutputFilePath);
    }

    @Test
    public void outputFileExistsTest() {
    	assertThat(expectedOutputFilePath).exists();
    }
    
}
