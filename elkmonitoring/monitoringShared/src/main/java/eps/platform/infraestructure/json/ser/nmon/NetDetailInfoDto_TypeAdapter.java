package eps.platform.infraestructure.json.ser.nmon;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import eps.platform.infraestructure.common.Constants;
import eps.platform.infraestructure.json.dto.nmon.NetDetailInfoDto;
import eps.platform.infraestructure.json.dto.nmon.NetDetailInfoDto.NetDetailInfoDtoBuilder;

/**
 * GSON type adapte that flattens {@code NetDetailInfoDto} json records
 *  
 * @author jdmoreno
 *
 */
public class NetDetailInfoDto_TypeAdapter extends TypeAdapter<NetDetailInfoDto> {

	@Override
	public void write(JsonWriter out, NetDetailInfoDto value) throws IOException {
	    out.beginObject();
	    writeBody(out, value);
	    out.endObject();
	}

	private void writeBody(JsonWriter out, NetDetailInfoDto value) throws IOException {
		out.name(Constants.RECORD_TYPE).value(value.getType());
		
		// Header
		HeaderDto_TypeAdapter.writeBody(out, value.getHeader());

		out.name("deviceId").value(value.getDeviceId());
		out.name("readKB").value(value.getReadKB());
		out.name("writeKB").value(value.getWriteKB());
	}

	@Override
	public NetDetailInfoDto read(JsonReader in) throws IOException {
		
	    in.beginObject();
	    NetDetailInfoDto value = readBody(in);
	    in.endObject();

		return value;
	}

	private NetDetailInfoDto readBody(JsonReader in) throws IOException {
		NetDetailInfoDtoBuilder builder = NetDetailInfoDto.builder();	    
	    while (in.hasNext()) {
	        String key = in.nextString();
//	        String value = in.nextString();
	        
	      switch (key) {
//	      case "type":
//	    	  headerDtoBuilder.(value);
//	    	  break;
	      default:
	    	  break;        
	      }
	    }
		return builder.build();
	}
}
