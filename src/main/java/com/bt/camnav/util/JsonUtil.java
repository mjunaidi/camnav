package com.bt.camnav.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.bt.camnav.type.DateDeserializer;
import com.bt.camnav.type.DateSerializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public enum JsonUtil {

    INSTANCE;

    public static final String EMPTY_JSON = "{}";
    public static final String EMPTY_JSON_ARR = "[]";
    public static final String NULL_JSON = "null";
    public static final String OK = "OK";
    public static final String FAILED = "FAILED";
    
    public static final JsonArray EMPTY_JSON_ARRAY = new JsonArray();
    public static final JsonObject EMPTY_JSON_OBJECT = new JsonObject();

    private final Gson gson;
    private final JsonParser parser;

    private JsonUtil() {
        GsonBuilder builder = new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer())
                .registerTypeAdapter(Date.class, new DateSerializer());
        gson = builder.create();
        parser = new JsonParser();
    }

    public Gson getGson() {
        return gson;
    }

    public JsonParser getJsonParser() {
        return parser;
    }

    public JsonElement parse(String json) {
        if (json != null && !json.isEmpty()) {
            return parser.parse(json);
        }
        return null;
    }

    public <T> T fromJson(JsonElement json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public JsonElement jsonize(Object obj) {
        return gson.toJsonTree(obj);
    }

    public String getStatus(boolean status) {
        return status ? OK : FAILED;
    }

    public JsonObject createBasicResponse(boolean status) {
        return createBasicResponse(status, "");
    }

    public JsonObject createBasicResponse(boolean status, String message) {
        JsonObject json = new JsonObject();

        json.addProperty("status", getStatus(status));

        if (message != null && !message.isEmpty()) {
            json.addProperty("message", message);
        }

        return json;
    }

    public JsonObject createSuccessResponse(boolean status, String message, Long id) {
        JsonObject json = createBasicResponse(status, message);
        if (json != null) {
            json.addProperty("id", id);
        }
        return json;
    }

    public JsonArray createJsonArrayFromList(List<String> list, String propertyName) {
        if (list != null && list.size() > 0) {
            JsonArray array = new JsonArray();

            for (String entry : list) {
                JsonObject json = new JsonObject();

                json.addProperty(propertyName, entry);

                array.add(json);
            }

            return array;
        }
        return null;
    }

    public JsonArray createJsonArrayFromDateKeys(List<String> dateKeys) {
        if (dateKeys != null && dateKeys.size() > 0) {
            JsonArray array = new JsonArray();

            for (String dateKey : dateKeys) {
                JsonObject json = new JsonObject();

                json.addProperty("dateKey", dateKey);

                Date date = DateFormatterUtil.INSTANCE.getDateFromString(dateKey);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int day = cal.get(Calendar.DATE);
                int month = cal.get(Calendar.MONTH) + 1;
                String monthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
                int year = cal.get(Calendar.YEAR);

                String dayStr = day < 10 ? "0" + day : String.valueOf(day);
                String monthStr = month < 10 ? "0" + month : String.valueOf(month);

                json.addProperty("day", dayStr);
                json.addProperty("month", monthStr);
                json.addProperty("monthName", monthName);
                json.addProperty("year", year);

                array.add(json);
            }

            return array;
        }
        return null;
    }

    protected String generateVarName(String name) {
        return String.valueOf(name.charAt(0)).toLowerCase().concat(name.substring(1));
    }

    protected String generateNameForMethod(String name) {
        return String.valueOf(name.charAt(0)).toUpperCase().concat(name.substring(1));
    }

    @SuppressWarnings("rawtypes")
    public Object nullifyLazyProxy(Object object) throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        Class clazz = object.getClass();

        Field[] fields = clazz.getDeclaredFields();
        Method[] methods = clazz.getMethods();

        if (fields != null) {
            for (Field field : fields) {
                field.setAccessible(true);

                OneToMany oneToMany = field.getAnnotation(OneToMany.class);

                boolean nullify = false;

                if (oneToMany != null && oneToMany.fetch().equals(FetchType.LAZY)) {
                    nullify = true;
                } else {
                    ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
                    if (manyToMany != null && manyToMany.fetch().equals(FetchType.LAZY)) {
                        nullify = true;
                    } else {
                        ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
                        if (manyToOne != null && manyToOne.fetch().equals(FetchType.LAZY)) {
                            nullify = true;
                        }
                    }
                }

                if (nullify) {
                    String fieldName = field.getName();
                    String fieldNameInMethod = generateNameForMethod(fieldName);
                    String setMethodName = "set".concat(fieldNameInMethod);

                    for (Method method : methods) {
                        String methodName = method.getName();

                        if (methodName.contains(setMethodName)) {
                            method.invoke(object, new Object[] { null });
                        }
                    }
                }
            }
        }

        return object;
    }
}