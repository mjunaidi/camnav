package com.bt.camnav.type;

import java.lang.reflect.Type;
import java.util.Collection;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class CollectionSerializer<E> implements JsonSerializer<Collection<E>> {

    @Override
    public JsonElement serialize(final Collection<E> collection, final Type type, final JsonSerializationContext context) {
        try {
            
        } catch (Exception e) {
            System.out.println("test");
        }
        return null;
    }
}
