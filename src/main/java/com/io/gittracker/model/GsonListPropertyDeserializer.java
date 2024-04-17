package com.io.gittracker.model;

import com.google.gson.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class GsonListPropertyDeserializer<T>
        implements JsonSerializer<ListProperty<T>>, JsonDeserializer<ListProperty<T>> {

    @Override
    public ListProperty<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        final JsonArray asJsonArray = json.getAsJsonArray();
        var ret = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<T>()));
        for (var element : asJsonArray) {
            T des = context.deserialize(element, ((ParameterizedType) typeOfT).getActualTypeArguments()[0]);
            ret.add(des);
        }
        return ret;
    }

    @Override
    public JsonElement serialize(ListProperty<T> src, Type typeOfSrc, JsonSerializationContext context) {
        var list = src.get().stream().toList();
        final JsonElement element = context.serialize(list);
        return element;
    }
}
