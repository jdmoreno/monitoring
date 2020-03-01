package nmon2json;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import eps.platform.infraestructure.exception.EPSMonioringException;
import eps.platform.infraestructure.internal.CPUInfo;

public class CPUInfoTest {
    @Test
    public void validCpuInfoRecordTest() throws EPSMonioringException, IOException {
    	CPUInfo cpuInfo = new CPUInfo();
    	
    	String headerRecord = "CPU_ALL,CPU Total epcrmg507,User%,Sys%,Wait%,Idle%,Busy,CPUs"; 
    	String[] cpuAllHeaders = StringUtils.splitPreserveAllTokens(headerRecord, ',');
    	
    	String sectionRecord = "CPU_ALL,T0003,0.2,0.3,0.0,99.5,,8";
    	String[] tokens = StringUtils.splitPreserveAllTokens(sectionRecord, ',');
    	
    	cpuInfo.recordCpuAll(cpuAllHeaders, tokens);
    	
    	assertThat(cpuInfo.getUser()).as("CPU User").isEqualTo(0.2);

    	assertThat(cpuInfo.getSys()).as("CPU Sys").isEqualTo(0.3);       

    	assertThat(cpuInfo.getWait()).as("CPU Wait").isEqualTo(0.0);              

    	assertThat(cpuInfo.getIdle()).as("CPU Idle").isEqualTo(99.5);              

    	assertThat(cpuInfo.getBusy()).as("CPU Busy").isEqualTo(0);              

    	assertThat(cpuInfo.getCpus()).as("CPU CPUs").isEqualTo(8);              
    	
    }

    @Test
    public void invalidDoubleTest() throws EPSMonioringException, IOException {
    	CPUInfo cpuInfo = new CPUInfo();
    	
    	String headerRecord = "CPU_ALL,CPU Total epcrmg507,User%,Sys%,Wait%,Idle%,Busy,CPUs"; 
    	String[] cpuAllHeaders = StringUtils.splitPreserveAllTokens(headerRecord, ',');
    	
    	String sectionRecord = "CPU_ALL,T0003,0.2,xxx,0.0,99.5,,8";
    	String[] tokens = StringUtils.splitPreserveAllTokens(sectionRecord, ',');
    	
    	cpuInfo.recordCpuAll(cpuAllHeaders, tokens);
    	
    	assertThat(cpuInfo.getUser()).as("CPU User").isEqualTo(0.2);

    	assertThat(cpuInfo.getSys()).as("CPU Sys").isEqualTo(0.0);       

    	assertThat(cpuInfo.getWait()).as("CPU Wait").isEqualTo(0.0);              

    	assertThat(cpuInfo.getIdle()).as("CPU Idle").isEqualTo(99.5);              

    	assertThat(cpuInfo.getBusy()).as("CPU Busy").isEqualTo(0);              

    	assertThat(cpuInfo.getCpus()).as("CPU CPUs").isEqualTo(8);              
    	
    }
    
    @Test
    public void invalidRecordTest() throws EPSMonioringException, IOException {
    	CPUInfo cpuInfo = new CPUInfo();
    	
    	String headerRecord = "CPU_ALL,CPU Total epcrmg507,User%,Sys%,Wait%,Idle%,Busy,CPUs"; 
    	String[] cpuAllHeaders = StringUtils.splitPreserveAllTokens(headerRecord, ',');
    	
    	String sectionRecord = "CPU_ALL,xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    	String[] tokens = StringUtils.splitPreserveAllTokens(sectionRecord, ',');
    	
    	cpuInfo.recordCpuAll(cpuAllHeaders, tokens);
    	
    	assertThat(cpuInfo.getUser()).as("CPU User").isEqualTo(0.0);

    	assertThat(cpuInfo.getSys()).as("CPU Sys").isEqualTo(0.0);       

    	assertThat(cpuInfo.getWait()).as("CPU Wait").isEqualTo(0.0);              

    	assertThat(cpuInfo.getIdle()).as("CPU Idle").isEqualTo(0.0);              

    	assertThat(cpuInfo.getBusy()).as("CPU Busy").isEqualTo(0);              

    	assertThat(cpuInfo.getCpus()).as("CPU CPUs").isEqualTo(0);              
    	
    }
    
}
