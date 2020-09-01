package eps.platform.infraestructure.json.ser.nmon;

import java.io.IOException;
import java.util.Map.Entry;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import eps.platform.infraestructure.dto.nmon.MemInfoDto;
import eps.platform.infraestructure.dto.nmon.MemInfoDto.MemInfoDtoBuilder;

public class MemInfoDto_TypeAdapter extends TypeAdapter<MemInfoDto> {

	@Override
	public void write(JsonWriter out, MemInfoDto value) throws IOException {
	    out.beginObject();
	    writeBody(out, value);
	    out.endObject();
	}

	public static void writeBody(JsonWriter out, MemInfoDto value) throws IOException {
		for (Entry<String, Double> entry : value.getMap().entrySet()) {
			out.name(entry.getKey()).value(entry.getValue());
		}
	}

	@Override
	public MemInfoDto read(JsonReader in) throws IOException {
		
	    in.beginObject();
	    MemInfoDto value = readBody(in);
	    in.endObject();

		return value;
	}

	protected static MemInfoDto readBody(JsonReader in) throws IOException {
		MemInfoDtoBuilder builder = MemInfoDto.builder();	    
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
