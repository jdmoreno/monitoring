package eps.platform.infraestructure.json.ser.nmon;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import eps.platform.infraestructure.json.dto.nmon.CpuInfoDto;
import eps.platform.infraestructure.json.dto.nmon.CpuInfoDto.CpuInfoDtoBuilder;

public class CpuInfoDto_TypeAdapter extends TypeAdapter<CpuInfoDto> {

	@Override
	public void write(JsonWriter out, CpuInfoDto value) throws IOException {
	    out.beginObject();
	    writeBody(out, value);
	    out.endObject();
	}

	public static void writeBody(JsonWriter out, CpuInfoDto value) throws IOException {
		out.name("user").value(value.getUser());
	    out.name("sys").value(value.getSys());
	    out.name("wait").value(value.getWait());
	    out.name("idle").value(value.getIdle());
	    out.name("busy").value(value.getBusy());
	    out.name("steal").value(value.getSteal());	    
	    out.name("cpus").value(value.getCpus());
	}

	@Override
	public CpuInfoDto read(JsonReader in) throws IOException {
		
	    in.beginObject();
	    CpuInfoDto value = readBody(in);
	    in.endObject();

		return value;
	}

	protected static CpuInfoDto readBody(JsonReader in) throws IOException {
		CpuInfoDtoBuilder builder = CpuInfoDto.builder();	    
	    while (in.hasNext()) {
	        String key = in.nextString();
//	        String value = in.nextString();
	        
	      switch (key) {
//	      case "source":
//	    	  headerDtoBuilder.source(value);
//	    	  break;
//	      case "section":
//	    	  headerDtoBuilder.section(value);
//	    	  break;
//	      case "timestamp":
//	    	  headerDtoBuilder.timestamp(value);
//	    	  break;
//	      case "hostname":
//	    	  headerDtoBuilder.hostname(value);
//	    	  break;
//	      case "os":
//	    	  headerDtoBuilder.os(value);
//	    	  break;
//	      case "dataCentre":
//	    	  headerDtoBuilder.dataCentre(value);
//	    	  break;
//	      case "environment":
//	    	  headerDtoBuilder.environment(value);
//	    	  break;
//	      case "serverFunction":
//	    	  headerDtoBuilder.serverFunction(value);
//	    	  break;
	      default:
	    	  break;        
	      }
	    }
		return builder.build();
	}
}
