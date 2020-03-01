package eps.platform.infraestructure.json.ser.nmon;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import eps.platform.infraestructure.common.Constants;
import eps.platform.infraestructure.json.dto.nmon.HostDto;
import eps.platform.infraestructure.json.dto.nmon.HostDto.HostDtoBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * GSON type adapte that flattens {@code HostDto} json records
 *  
 * @author jdmoreno
 *
 */
@Slf4j
public class HostDto_TypeAdapter extends TypeAdapter<HostDto> {

	@Override
	public void write(JsonWriter out, HostDto value) throws IOException {
	    out.beginObject();
	    writeBody(out, value);
	    out.endObject();
	}

	private void writeBody(JsonWriter out, HostDto value) throws IOException {
		out.name(Constants.RECORD_TYPE).value(value.getType());
		
		// Header
		HeaderDto_TypeAdapter.writeBody(out, value.getHeader());
		
		// Cpu
		CpuInfoDto_TypeAdapter.writeBody(out, value.getCpuInfo());
		
		// Memory
		MemInfoDto_TypeAdapter.writeBody(out, value.getMemInfo());
		
		// Net
		NetInfoDto_TypeAdapter.writeBody(out, value.getNetInfo());
		
	}

	@Override
	public HostDto read(JsonReader in) throws IOException {
		
	    in.beginObject();
	    HostDto value = readBody(in);
	    in.endObject();

		return value;
	}

	private HostDto readBody(JsonReader in) throws IOException {
		HostDtoBuilder builder = HostDto.builder();	    
	    while (in.hasNext()) {
	        String key = in.nextString();
	        String value = in.nextString();	        
	      switch (key) {
//	      case "type":
//	    	  headerDtoBuilder.(value);
//	    	  break;
	      default:
	    	  log.info("Read key: {} value: {}", key, value);
	    	  break;        
	      }
	    }
		return builder.build();
	}
}
