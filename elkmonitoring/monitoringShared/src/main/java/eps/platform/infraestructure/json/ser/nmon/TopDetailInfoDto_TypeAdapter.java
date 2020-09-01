package eps.platform.infraestructure.json.ser.nmon;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import eps.platform.infraestructure.common.Constants;
import eps.platform.infraestructure.dto.nmon.TopDetailInfoDto;
import eps.platform.infraestructure.dto.nmon.TopDetailInfoDto.TopDetailInfoDtoBuilder;

/**
 * GSON type adapte that flattens {@code TopDetailInfoDto} json records
 *  
 * @author jdmoreno
 *
 */
public class TopDetailInfoDto_TypeAdapter extends TypeAdapter<TopDetailInfoDto> {

	@Override
	public void write(JsonWriter out, TopDetailInfoDto value) throws IOException {
	    out.beginObject();
	    writeBody(out, value);
	    out.endObject();
	}

	private void writeBody(JsonWriter out, TopDetailInfoDto value) throws IOException {
		out.name(Constants.RECORD_TYPE).value(value.getType());
		
		// Header
		HeaderDto_TypeAdapter.writeBody(out, value.getHeader());

		out.name("command").value(value.getCommand());
		out.name("pid").value(value.getPid());
		out.name("cpus").value(value.getCpus());
		out.name("cpu").value(value.getCpu());
		out.name("usr").value(value.getUsr());
		out.name("sys").value(value.getSys());
		out.name("size").value(value.getSize());
		out.name("resText").value(value.getResText());
		out.name("resData").value(value.getResData());
	}

	@Override
	public TopDetailInfoDto read(JsonReader in) throws IOException {
		
	    in.beginObject();
	    TopDetailInfoDto value = readBody(in);
	    in.endObject();

		return value;
	}

	private TopDetailInfoDto readBody(JsonReader in) throws IOException {
		TopDetailInfoDtoBuilder builder = TopDetailInfoDto.builder();	    
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
