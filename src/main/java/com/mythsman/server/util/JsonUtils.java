package com.mythsman.server.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author tusenpo
 * @date 6/5/21
 */
public class JsonUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    private static final ObjectMapper camelMapper = new ObjectMapper();
    private static final ObjectMapper snakeMapper = new ObjectMapper();

    static {
        camelMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        camelMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        camelMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        camelMapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);

        camelMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        camelMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        camelMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        camelMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    public static ObjectMapper getCamelMapper() {
        return camelMapper;
    }

    public static ObjectMapper getSnakeMapper() {
        return snakeMapper;
    }

    public static String toJson(Object object) {
        try {
            return camelMapper.writeValueAsString(object);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return (T) camelMapper.readValue(json, clazz);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return camelMapper.readValue(json, typeReference);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

}
