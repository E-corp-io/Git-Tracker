package com.io.gittracker.utils;

import com.google.gson.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

public class GsonOptionalDeserializer<T> implements JsonSerializer<Optional<T>>, JsonDeserializer<Optional<T>> {

    @Override
    public Optional<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        final JsonArray asJsonArray = json.getAsJsonArray();
        final JsonElement jsonElement = asJsonArray.get(0);
        final T value = context.deserialize(jsonElement, ((ParameterizedType) typeOfT).getActualTypeArguments()[0]);
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(value);
    }

    @Override
    public JsonElement serialize(Optional<T> src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonElement element = context.serialize(src.orElse(null));
        final JsonArray result = new JsonArray();
        result.add(element);
        return result;
    }
}
