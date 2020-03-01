package nmon2json;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import eps.platform.infraestructure.nmon.NMONFile;
import eps.platform.infraestructure.nmon.NMONHeader;

public class NMONFileTest {

	static NMONFile nmonFile = null;
	
	@BeforeAll 
    public static void initialize() throws Exception {
    	nmonFile = new NMONFile(Paths.get("./src/test/resources/99.NMON/BBP/2018-11/2018-11-01/epcrmg506_181101_0000.nmon"), "BBP");
    	nmonFile.process();
     }

    @Test
    public void nmonInputFilePathTest() throws Exception {
    		assertThat(nmonFile.getInputFilePath()).isEqualTo(Paths.get("./src/test/resources/99.NMON/BBP/2018-11/2018-11-01/epcrmg506_181101_0000.nmon"));
    }

    @Test    
    public void nmonFileNameTest() throws Exception {
		assertThat(nmonFile.getFileName()).isEqualTo("epcrmg506_181101_0000.nmon");
    }

    @Test    
    public void nmonDataCentreTest() throws Exception {
		assertThat(nmonFile.getDataCentre()).isEqualTo("BBP");
    }
    
    @Test    
    public void nmonNumberOfSectionsTest() throws Exception {
		assertThat(nmonFile.getNmonSections().size()).isEqualTo(1441);
    }

    @Test    
    public void nmonHostNameTest() throws Exception {
    	NMONHeader nmonHeader = nmonFile.getNmonHeader();
    	
		assertThat(nmonHeader.getHostname()).isEqualTo("epcrmg506");
    }

    @Test    
    public void nmonDateTest() throws Exception {
    	NMONHeader nmonHeader = nmonFile.getNmonHeader();
    	
		assertThat(nmonHeader.getDate()).isEqualTo(LocalDate.of(2018, Month.NOVEMBER, 1));
    }
        
}
