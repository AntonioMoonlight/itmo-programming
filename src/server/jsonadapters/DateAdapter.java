package server.jsonadapters;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateAdapter  implements JsonSerializer<Date>, JsonDeserializer<Date> {

    public static final String PATTERN = "yyyy-MM-dd";
    public static final SimpleDateFormat formatter = new SimpleDateFormat(PATTERN);

    @Override
    public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(formatter.format(date));
    }

    @Override
    public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {
        try {
            return formatter.parse(jsonElement.getAsString());
        } catch (ParseException e) {
            throw new JsonParseException("Failed to parse Date: " + jsonElement.getAsString(), e);
        }
    }
}