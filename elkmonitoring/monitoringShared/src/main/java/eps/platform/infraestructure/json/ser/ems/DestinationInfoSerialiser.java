package eps.platform.infraestructure.json.ser.ems;

import java.lang.reflect.Type;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import eps.platform.infraestructure.common.Constants;
import eps.platform.infraestructure.json.dto.ems.DestinationInfoDto;

public class DestinationInfoSerialiser implements JsonSerializer<DestinationInfoDto> { 
	@Override
	public JsonElement serialize(DestinationInfoDto src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        
	    if (src == null) {
	    	return result;
	    }
	    
	    result.add(Constants.RECORD_TYPE, new JsonPrimitive(src.getType()));
	    
	    if (src.getHeader()!=null) {
		    result.add("timestamp", new JsonPrimitive(StringUtils.defaultString(src.getHeader().getTimestamp())));
		    result.add("hostname", new JsonPrimitive(StringUtils.defaultString(src.getHeader().getHostname())));
		    result.add("dataCentre", new JsonPrimitive(StringUtils.defaultString(src.getHeader().getDataCentre())));
		    result.add("environment", new JsonPrimitive(StringUtils.defaultString(src.getHeader().getEnvironment())));
		    result.add("serverFunction", new JsonPrimitive(StringUtils.defaultString(src.getHeader().getServerFunction())));
	    }
	    
        result.add("serverName", new JsonPrimitive(StringUtils.defaultString(src.getServerName())));
        result.add("hostName", new JsonPrimitive(StringUtils.defaultString(src.getHostName())));
        result.add("destinationName", new JsonPrimitive(StringUtils.defaultString(src.getDestinationName())));
        result.add("destinationType", new JsonPrimitive(StringUtils.defaultString(src.getDestinationType())));
        result.add("store", new JsonPrimitive(StringUtils.defaultString(StringUtils.defaultString(src.getStore()))));
        result.add("static", new JsonPrimitive(src.isStaticSw()));
        result.add("temporary", new JsonPrimitive(src.isTemporary()));
	
        final JsonElement jsonStats = context.serialize(src.getStats());
        result.add("stats", jsonStats);

        final JsonElement jsonStatsInboud = context.serialize(src.getStatsInbound());
        result.add("statsInbound", jsonStatsInboud);

        final JsonElement jsonStatsOutbound = context.serialize(src.getStatsInbound());
        result.add("statsOutbound", jsonStatsOutbound);

		return result;
	}	
	
}
