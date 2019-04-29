package com.example.httpclient.httpClient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by zhouwei on 2019/4/29
 **/
public class JsonUtils {
    public static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> T convertToObject(String jsonStr, Class<T> valueType) {
        try {
            return jsonStr != null && jsonStr != "" ? objectMapper.readValue(jsonStr, valueType) : null;
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static <T> T convertToObject(String jsonStr, TypeReference<T> valueTypeRef) {
        try {
            return jsonStr != null && jsonStr != "" ? objectMapper.readValue(jsonStr, valueTypeRef) : null;
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static <T> T convertToObject(String jsonStr, JavaType javaType) {
        try {
            return jsonStr != null && jsonStr != "" ? objectMapper.readValue(jsonStr, javaType) : null;
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static String convertToJson(Object object) {
        try {
            return object == null ? null : objectMapper.writeValueAsString(object);
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public static JavaType BuildJavaType(Class<?> c1, Class<?> c2) {
        return objectMapper.getTypeFactory().constructParametricType(c1, new Class[]{c2});
    }

}
