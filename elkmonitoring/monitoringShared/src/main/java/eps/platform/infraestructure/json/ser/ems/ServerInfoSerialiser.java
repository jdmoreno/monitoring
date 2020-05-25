package eps.platform.infraestructure.json.ser.ems;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import eps.platform.infraestructure.common.Constants;
import eps.platform.infraestructure.json.dto.ems.ServerInfoDto;

public class ServerInfoSerialiser implements JsonSerializer<ServerInfoDto> { 
	@Override
	public JsonElement serialize(ServerInfoDto src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        
	    if (src == null) {
	    	return result;
	    }
	    
	    result.add(Constants.RECORD_TYPE, new JsonPrimitive(src.getType()));
	    
	    if (src.getHeader()!=null) {
		    result.add("timestamp", new JsonPrimitive(src.getHeader().getTimestamp()));
		    result.add("hostname", new JsonPrimitive(src.getHeader().getHostname()));
		    result.add("dataCentre", new JsonPrimitive(src.getHeader().getDataCentre()));
		    result.add("environment", new JsonPrimitive(src.getHeader().getEnvironment()));
		    result.add("serverFunction", new JsonPrimitive(src.getHeader().getServerFunction()));
	    }
	    
        result.add("serverName", new JsonPrimitive(src.getServerName()));
//      result.add("hostname", new JsonPrimitive(src.getHostname()));
        result.add("url", new JsonPrimitive(src.getUrl()));
	
        final JsonElement jsonStats = context.serialize(src.getStats());
        result.add("stats", jsonStats);

		return result;
	}	
	
}
