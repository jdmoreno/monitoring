package eps.platform.infraestructure.json;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import eps.platform.infraestructure.dto.nmon.HostDto;

public class FlatJSONSerializer implements JsonSerializer<HostDto> {
	@Override
	public JsonElement serialize(HostDto src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject jObject = new JsonObject();

		String stringValue = "id";		
		jObject.addProperty(stringValue, src.getType());

		return jObject;
	}
}
