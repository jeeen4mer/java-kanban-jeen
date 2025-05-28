package adapter;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;

public class GsonAdapters {
    public static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.toString());
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return json.isJsonNull() ? null : LocalDateTime.parse(json.getAsString());
        }
    }

    public static class DurationAdapter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {
        @Override
        public JsonElement serialize(Duration duration, Type typeOfSrc, JsonSerializationContext context) {
            return duration == null ? JsonNull.INSTANCE : new JsonPrimitive(duration.toString());
        }

        @Override
        public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            if (json.isJsonNull()) {
                return null;
            }
            try {
                if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
                    return Duration.parse(json.getAsString());
                }
                if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
                    return Duration.ofMillis(json.getAsLong());
                }
                throw new JsonParseException("Unexpected duration format: " + json);
            } catch (Exception e) {
                throw new JsonParseException("Error parsing duration: " + json, e);
            }
        }
    }

}