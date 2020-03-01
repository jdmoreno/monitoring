package eps.platform.infraestructure.json.ser.nmon;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import eps.platform.infraestructure.common.Constants;
import eps.platform.infraestructure.json.dto.nmon.DiskDetailInfoDto;
import eps.platform.infraestructure.json.dto.nmon.DiskDetailInfoDto.DiskDetailInfoDtoBuilder;

/**
 * GSON type adapte that flattens {@code DiskDetailInfoDto} json records
 *  
 * @author jdmoreno
 *
 */
public class DiskDetailInfoDto_TypeAdapter extends TypeAdapter<DiskDetailInfoDto> {

	@Override
	public void write(JsonWriter out, DiskDetailInfoDto value) throws IOException {
	    out.beginObject();
	    writeBody(out, value);
	    out.endObject();
	}

	private void writeBody(JsonWriter out, DiskDetailInfoDto value) throws IOException {
		out.name(Constants.RECORD_TYPE).value(value.getType());
		
		// Header
		HeaderDto_TypeAdapter.writeBody(out, value.getHeader());

		out.name("diskId").value(value.getDiskId());
		out.name("diskBusy").value(value.getDiskBusy());
		out.name("diskRead").value(value.getDiskRead());
		out.name("diskWrite").value(value.getDiskWrite());
		out.name("diskXfer").value(value.getDiskXfer());
		out.name("diskBsize").value(value.getDiskBsize());
	}

	@Override
	public DiskDetailInfoDto read(JsonReader in) throws IOException {
		
	    in.beginObject();
	    DiskDetailInfoDto value = readBody(in);
	    in.endObject();

		return value;
	}

	private DiskDetailInfoDto readBody(JsonReader in) throws IOException {
		DiskDetailInfoDtoBuilder builder = DiskDetailInfoDto.builder();	    
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
