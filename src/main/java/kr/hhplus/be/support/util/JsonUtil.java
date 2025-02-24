package kr.hhplus.be.support.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())  // Java 8 날짜/시간 지원 모듈 추가
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false); // ISO 8601 형식으로 변환

    // 객체 -> JSON (직렬화)
    public static String toJson(Object object) {
        try {
            System.out.println("Object content: " + object);
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 직렬화 실패", e);
        }
    }

    // JSON -> 객체 (역직렬화)
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 역직렬화 실패", e);
        }
    }
}
