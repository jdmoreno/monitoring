package nmon2json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import eps.platform.infraestructure.NMON2JSON;
import eps.platform.infraestructure.cli.ApplicationCLI;
import eps.platform.infraestructure.exception.EPSMonioringException;


public class NMON2JSONTest {

//	NMON2JSON nmon2json = new NMON2JSON();
	
	/***
	 * No input file
	 * @throws EPSMonioringException 
	 */
    @Test
    public void noInputFileTest() throws EPSMonioringException {

    	String[] arguments = {"-i", "./src/test/resources/99.NMON", "-o", "./src/test/resources/99.JSON/BBP.json", "-da", "2018-12-06"};
    	
    	NMON2JSON nmon2json = new NMON2JSON();
		nmon2json.run(arguments);
    	
    	assertThat(nmon2json.getFilesProcessed()).isEqualTo(0);
    }

	/***
	 * Help argument
	 * @throws Exception 
	 */    
    @Test
    public void helpMessageHelpTest() throws Exception {

    	String[] arguments = {"-help"}; 
    	NMON2JSON nmon2json = new NMON2JSON();
    	nmon2json.run(arguments);
    }
    
	/***
	 * Invalid argument l
	 */    
    @Test
    public void invalidParameterTest() {

    	String[] arguments = {"-i", "./src/test/resources/99.NMON", "-o", "./src/test/resources/99.JSON/BBP.json", "-da", "2018-12-06", "-l"}; 
    	
    	NMON2JSON nmon2json = new NMON2JSON();
    	Throwable thrown = catchThrowable(() -> { nmon2json.run(arguments);});
    	
    	assertThat(thrown).isInstanceOf(EPSMonioringException.class)
        	.hasStackTraceContaining("Unrecognized option: -l");              
    }
    
	/***
	 * Two file processed
	 * @throws EPSMonioringException 
	 */
    @Test
    public void twoNMONFilesProcessedTest() throws EPSMonioringException {

    	String[] arguments = {"-i", "./src/test/resources/99.NMON", "-o", "./src/test/resources/99.JSON/BBP.json", "-dc", "BBP"};
    	
    	NMON2JSON nmon2json = new NMON2JSON();
		nmon2json.run(arguments);
    	
    	assertThat(nmon2json.getFilesProcessed()).isEqualTo(2);
    }

    /***
     * Check NMON2JSON behaviour with an input file is not a valid NMON
     * @throws EPSMonioringException
     */
    @Test
    public void invalidNmonFileTest() throws EPSMonioringException {

    	String[] arguments = {"-i", "./src/test/resources/sample.pdf", "-o", "./src/test/resources/99.JSON/BBP.json", "-da", "2018-12-06"};
    	
    	NMON2JSON nmon2json = new NMON2JSON();
		nmon2json.run(arguments);
    	
    	assertThat(nmon2json.getFilesProcessed()).isEqualTo(0);
    }

    /***
     * Check NMON2JSON behaviour with an input file is a valid NMON
     * @throws EPSMonioringException
     */    
    @Test
    public void validNmonFileTest() throws EPSMonioringException {

    	String[] arguments = {"-i", "./src/test/resources/99.NMON/BBP/2018-11/2018-11-01/epcrmg506_181101_0000.nmon", "-o", "./src/test/resources/99.JSON/BBP.json", "-da", "2018-12-06"};
    	
    	NMON2JSON nmon2json = new NMON2JSON();
		nmon2json.run(arguments);
    	
    	assertThat(nmon2json.getFilesProcessed()).isEqualTo(1);
    }
    
    /***
     * Check NMON2JSON behaviour with an invalid input path
     * @throws EPSMonioringException
     * 
     */ 
    @Disabled ("Ignored because of the differences between Windows and Linux paths")
    @Test
    public void invalidInputPathTest() throws EPSMonioringException {

    	String[] arguments = {"-i", ":: This is an invalid path ::", "-o", "./src/test/resources/99.JSON/BBP.json", "-da", "2018-12-06"};
    	
    	NMON2JSON nmon2json = new NMON2JSON();
    	Throwable thrown = catchThrowable(() -> { nmon2json.run(arguments);});
    	
    	assertThat(thrown).isInstanceOf(EPSMonioringException.class);              
    }
    
    /***
     * Check NMON2JSON behaviour with an invalid output path
     * @throws EPSMonioringException
     */    
    @Test
    public void invalidOutputPathTest() throws EPSMonioringException {

    	String[] arguments = {"-i", "./src/test/resources/99.JSON/BBP.json", "-o", ":: This is an invalid path ::", "-da", "2018-12-06"}; 
    	
    	NMON2JSON nmon2json = new NMON2JSON();
    	Throwable thrown = catchThrowable(() -> { nmon2json.run(arguments);});
    	
    	assertThat(thrown).isInstanceOf(EPSMonioringException.class);              
    }
    
    @Test
    public void datesList() throws EPSMonioringException {

    	String[] arguments = {"-i", "./src/test/resources/99.NMON/BBP/2018-11/2018-11-01/epcrmg506_181101_0000.nmon", "-o", "./src/test/resources/99.JSON/BBP.json", 
        "-dl", "./src/test/resources/config/dates.txt"}; 
		
    	NMON2JSON nmon2json = new NMON2JSON();
    	nmon2json.run(arguments);
    	
    	assertThat(nmon2json.getFilesProcessed()).isEqualTo(1);
    }

    @Test
    public void hostListTest() throws EPSMonioringException {

    	String[] arguments = {"-i", "./src/test/resources/99.NMON/BBP/2018-11/2018-11-01/epcrmg506_181101_0000.nmon", "-o", "./src/test/resources/99.JSON/BBP.json", "-da", "2018-12-06",
        "-hl", "./src/test/resources/config/hostnames.txt"};
    	
    	NMON2JSON nmon2json = new NMON2JSON();
		nmon2json.run(arguments);
    	
    	assertThat(nmon2json.getFilesProcessed()).isEqualTo(1);
    }
    
    @Test
    public void datesListAndDateTest() throws EPSMonioringException {

    	String[] arguments = {"-i", "./src/test/resources/99.NMON/BBP/2018-11/2018-11-01/epcrmg506_181101_0000.nmon", "-o", "./src/test/resources/99.JSON/BBP.json", "-da", "2018-12-06",
        "-dl", "./src/test/resources/config/dates.txt"}; 
    	
    	NMON2JSON nmon2json = new NMON2JSON();
    	Throwable thrown = catchThrowable(() -> { nmon2json.run(arguments);});
    	
    	assertThat(thrown).isInstanceOf(EPSMonioringException.class);              
    }
    
    /***
     * Check valid relative date
     * 
     * @throws EPSMonioringException
     */
    @Test
    public void validRelativeDateTest() throws EPSMonioringException {

    	int relativeDate = 2;
    	String[] arguments = {"-i", "./src/test/resources/99.NMON/BBP/2018-11/2018-11-01/epcrmg506_181101_0000.nmon", "-o", "./src/test/resources/99.JSON/BBP.json", "-dr", String.valueOf(relativeDate), "ch"}; 
    	
    	ApplicationCLI applicationCLI = new ApplicationCLI();
    	applicationCLI.checkConfig(arguments);		
    	
    	assertThat(applicationCLI.getDate()).isEqualTo(LocalDate.now().minusDays(relativeDate));
    }

    /***
     * Check invalid relative date with an alphanumeric parameter
     * 
     * @throws EPSMonioringException
     */    
    @Test
    public void invalidRelativeDate01Test() throws EPSMonioringException {

    	String[] arguments = {"-i", "./src/test/resources/99.NMON/BBP/2018-11/2018-11-01/epcrmg506_181101_0000.nmon", "-o", "./src/test/resources/99.JSON/BBP.json", "-dr", "aa", "-ch"}; 
    	
    	NMON2JSON nmon2json = new NMON2JSON();
    	Throwable thrown = catchThrowable(() -> { nmon2json.run(arguments);});
    	
    	assertThat(thrown).isInstanceOf(EPSMonioringException.class);              
    }

    /***
     * Check invalid relative date with an negative numeric parameter
     * 
     * @throws EPSMonioringException
     */    
    @Test
    public void invalidRelativeDate02Test() throws EPSMonioringException {

    	String[] arguments = {"-i", "./src/test/resources/99.NMON/BBP/2018-11/2018-11-01/epcrmg506_181101_0000.nmon", "-o", "./src/test/resources/99.JSON/BBP.json", "-dr", "-1", "-ch"}; 
    	
    	NMON2JSON nmon2json = new NMON2JSON();
    	Throwable thrown = catchThrowable(() -> { nmon2json.run(arguments);});
    	
    	assertThat(thrown).isInstanceOf(EPSMonioringException.class);              
    }
    
    @Test
    public void referenceLoadTest() throws EPSMonioringException {

    	String[] arguments = {"-i", "./src/test/resources/99.NMON/BBP/2018-11/2018-11-01/epcrmg506_181101_0000.nmon", "-o", "./src/test/resources/99.JSON/BBP.json", 
        "-r", "./src/test/resources/config/eps_infra_complete.csv"}; 
    	
    	NMON2JSON nmon2json = new NMON2JSON();
		nmon2json.run(arguments);
    	
    	assertThat(nmon2json.getFilesProcessed()).isEqualTo(1);
    }

    /***
     * Test processing an AIX nmon file
     * 
     * @throws EPSMonioringException
     */
    @Test
    public void processAixFileTest() throws EPSMonioringException {

    	String[] arguments = {"-i", "./src/test/resources/99.NMON/rmubbpxpv5004_181101_0000.nmon", "-o", "./src/test/resources/99.JSON/BBP.json", "-da", "2018-12-06"};
    	
    	NMON2JSON nmon2json = new NMON2JSON();
		nmon2json.run(arguments);
    	
    	assertThat(nmon2json.getFilesProcessed()).isEqualTo(1);
    }

    @Test
    public void processAllFilesTest() throws EPSMonioringException, IOException {
    	String outputFile = "./src/test/resources/99.JSON/ALL_FILES_TEST.json";
    	Path outputFilePath = Paths.get(outputFile);		
		Files.deleteIfExists(outputFilePath);

    	String[] arguments = {"-i", "./src/test/resources/99.NMON", "-o", outputFile};
    	
    	NMON2JSON nmon2json = new NMON2JSON();
		nmon2json.run(arguments);
    	
    	assertThat(nmon2json.getFilesProcessed()).isEqualTo(5);
    	assertThat(outputFilePath).exists();
    }
    
    /***
     * Check version
     * 
     */    
    @Test
    public void getVersionTest() throws EPSMonioringException {

    	String[] arguments = {"-v"}; 
    	
    	NMON2JSON nmon2json = new NMON2JSON();
    	nmon2json.run(arguments);
    	
    	assertThat(NMON2JSON.getVersion()).isEqualTo("0.0.10-SNAPSHOT");
    }
    
    
//    @Test
//    public void mainProcess() throws EPSMonioringException {
//
//    	String[] arguments = {"-i", "./src/test/resources/99.NMON/BBP/2018-11/2018-11-01/epcrmg506_181101_0000.nmon", "-o", "./src/test/resources/JSON/BBP.json", "-d", "2018-12-06",
//        "-hl", "./src/test/resources/config/hostnames.txt"}; 
//    	NMON2JSON.main(arguments);
//    	
//    	assertThat(nmon2json.getFilesProcessed()).isEqualTo(1);
//    }
    
}
