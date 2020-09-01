package eps.platform.infraestructure.json.ser.nmon;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import eps.platform.infraestructure.dto.nmon.HeaderDto;
import eps.platform.infraestructure.dto.nmon.HeaderDto.HeaderDtoBuilder;

public class HeaderDto_TypeAdapter extends TypeAdapter<HeaderDto> {

	@Override
	public void write(JsonWriter out, HeaderDto value) throws IOException {
	    out.beginObject();
	    writeBody(out, value);
	    out.endObject();
	}

	public static void writeBody(JsonWriter out, HeaderDto value) throws IOException {
		out.name("source").value(value.getSource());
	    out.name("section").value(value.getSection());
	    out.name("timestamp").value(value.getTimestamp());
	    out.name("hostname").value(value.getHostname());
	    out.name("os").value(value.getOs());
	    out.name("dataCentre").value(value.getDataCentre());	    
	    out.name("environment").value(value.getEnvironment());
	    out.name("serverFunction").value(value.getServerFunction());
	}

	@Override
	public HeaderDto read(JsonReader in) throws IOException {
		
	    in.beginObject();
	    HeaderDto value = readBody(in);
	    in.endObject();

		return value;
	}

	protected static HeaderDto readBody(JsonReader in) throws IOException {
		HeaderDtoBuilder builder = HeaderDto.builder();	    
	    while (in.hasNext()) {
	        String key = in.nextString();
	        String value = in.nextString();
	        
	      switch (key) {
	      case "source":
	    	  builder.source(value);
	    	  break;
	      case "section":
	    	  builder.section(value);
	    	  break;
	      case "timestamp":
	    	  builder.timestamp(value);
	    	  break;
	      case "hostname":
	    	  builder.hostname(value);
	    	  break;
	      case "os":
	    	  builder.os(value);
	    	  break;
	      case "dataCentre":
	    	  builder.dataCentre(value);
	    	  break;
	      case "environment":
	    	  builder.environment(value);
	    	  break;
	      case "serverFunction":
	    	  builder.serverFunction(value);
	    	  break;
	      default:
	    	  break;        
	      }
	    }
		return builder.build();
	}
}
