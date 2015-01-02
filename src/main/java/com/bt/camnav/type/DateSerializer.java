package com.bt.camnav.type;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DateSerializer implements JsonSerializer<Date> {

    @Override
    public JsonElement serialize(final Date date, final Type type, final JsonSerializationContext context) {
        if (date != null) {
            long time = date.getTime();
            Number number = Long.valueOf(time);
            return new JsonPrimitive(number);
        }
        return null;
    }
}
