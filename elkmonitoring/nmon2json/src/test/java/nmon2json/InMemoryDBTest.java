package nmon2json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import eps.platform.infraestructure.exception.EPSMonioringException;
import eps.platform.infraestructure.nmon.csv.InMemoryDB;
import eps.platform.infraestructure.nmon.csv.Reference;

public class InMemoryDBTest {

    @Test
    public void loadTest() throws EPSMonioringException, IOException {
    	Path referenceCSVPath = Paths.get("./src/test/resources/config/eps_infra_complete.csv");		
    	InMemoryDB.loadReferenceCSV(referenceCSVPath);    	
    }

    @Test
    public void invalidFileTest() throws EPSMonioringException, IOException {
    	Path referenceCSVPath = Paths.get("./src/test/resources/sample.pdf");		
    	Throwable thrown = catchThrowable(() -> { InMemoryDB.loadReferenceCSV(referenceCSVPath);});
    	
    	assertThat(thrown).isInstanceOf(EPSMonioringException.class)
        	.hasStackTraceContaining("sample.pdf");              
    }

    @Test
    public void nonExistingFileTest() throws EPSMonioringException, IOException {
    	Path referenceCSVPath = Paths.get("./src/test/resources/nonExisting.pdf");		
    	Throwable thrown = catchThrowable(() -> { InMemoryDB.loadReferenceCSV(referenceCSVPath);});
    	
    	assertThat(thrown).isInstanceOf(EPSMonioringException.class)
        	.hasStackTraceContaining("nonExisting.pdf");              
    }

    @Test
    public void existingHostnameTest() throws EPSMonioringException, IOException {
    	Path referenceCSVPath = Paths.get("./src/test/resources/config/eps_infra_complete.csv");
    	InMemoryDB.loadReferenceCSV(referenceCSVPath);    
    	Reference values = InMemoryDB.get("EPCRMG2049");
    	
    	assertThat(values.getHostname()).isEqualTo("EPCRMG2049");

    	assertThat(values.getEnvironment()).isEqualTo("PREPROD");       

    	assertThat(values.getDataCentre()).isEqualTo("LBG");              

    	assertThat(values.getComponent()).isEqualTo("TDF");              

    	assertThat(values.getServerFunction()).isEqualTo("TDF");              
    	
    }

    @Test
    public void existingHostnameCaseTest() throws EPSMonioringException, IOException {
    	Path referenceCSVPath = Paths.get("./src/test/resources/config/eps_infra_complete.csv");
    	InMemoryDB.loadReferenceCSV(referenceCSVPath);    
    	Reference values = InMemoryDB.get("EPCRmg2049");
    	
    	assertThat(values.getHostname()).isEqualTo("EPCRMG2049");

    	assertThat(values.getEnvironment()).isEqualTo("PREPROD");       

    	assertThat(values.getDataCentre()).isEqualTo("LBG");              

    	assertThat(values.getComponent()).isEqualTo("TDF");              

    	assertThat(values.getServerFunction()).isEqualTo("TDF");              
    	
    }
    
}
