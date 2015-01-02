package com.bt.camnav.type;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

public class DateDeserializer implements JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        if (json instanceof JsonPrimitive) {
            final JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive();
            
            long time = jsonPrimitive.getAsLong();
            
            Date date = new Date(time);
            
            return date;
        }
        return null;
    }
}
