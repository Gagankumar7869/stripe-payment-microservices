package com.gagan.payments.util;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JsonUtil {

    // Injected via constructor by Lombok's @RequiredArgsConstructor
    private final ObjectMapper objectMapper;

    public <T> T convertJsonToObject(String jsonString, Class<T> clazz) {
        if (jsonString == null || clazz == null) {
            log.warn("convertJsonToObject called with null input: jsonString={} clazz={}", jsonString, clazz);
            return null;
        }
        try {
            T result = objectMapper.readValue(jsonString, clazz);
            log.debug("Successfully converted JSON to {}: {}", clazz.getSimpleName(), result);
            return result;
        } catch (Exception e) {
            log.error("Failed to convert JSON to {}. jsonString={}", clazz != null ? clazz.getName() : "null", jsonString, e);
            return null;
        }
    }

    public String convertObjectToJson(Object obj) {
        if (obj == null) {
            log.warn("convertObjectToJson called with null object");
            return null;
        }
        try {
            String json = objectMapper.writeValueAsString(obj);
            log.debug("Successfully converted object to JSON: {}", json);
            return json;
        } catch (JsonProcessingException e) {
            log.error("Failed to convert object to JSON: {}", obj, e);
            return null;
        }
    }
}