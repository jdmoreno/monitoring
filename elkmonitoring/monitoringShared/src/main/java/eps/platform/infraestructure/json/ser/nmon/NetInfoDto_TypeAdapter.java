package eps.platform.infraestructure.json.ser.nmon;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import eps.platform.infraestructure.json.dto.nmon.NetInfoDto;
import eps.platform.infraestructure.json.dto.nmon.NetInfoDto.NetInfoDtoBuilder;

public class NetInfoDto_TypeAdapter extends TypeAdapter<NetInfoDto> {

	@Override
	public void write(JsonWriter out, NetInfoDto value) throws IOException {
	    out.beginObject();
	    writeBody(out, value);
	    out.endObject();
	}

	public static void writeBody(JsonWriter out, NetInfoDto value) throws IOException {
		out.name("readKB").value(value.getReadKB());
		out.name("writeKB").value(value.getWriteKB());
	}

	@Override
	public NetInfoDto read(JsonReader in) throws IOException {
		
	    in.beginObject();
	    NetInfoDto value = readBody(in);
	    in.endObject();

		return value;
	}

	protected static NetInfoDto readBody(JsonReader in) throws IOException {
		NetInfoDtoBuilder builder = NetInfoDto.builder();	    
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
