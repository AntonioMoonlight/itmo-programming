package Controller.JsonAdapters;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter  implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(localDateTime.format(formatter));
    }

    @Override
    public LocalDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {
        try {
            return LocalDateTime.parse(jsonElement.getAsString(), formatter);
        } catch (DateTimeException e) {
            throw new JsonParseException("Failed to parse LocalDateTime: " + jsonElement.getAsString(), e);
        }
    }
}
