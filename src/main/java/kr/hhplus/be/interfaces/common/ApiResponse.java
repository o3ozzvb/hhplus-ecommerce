package kr.hhplus.be.interfaces.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    private final boolean success; // 요청 성공 여부
    private final String message;  // 응답 메시지
    private final T data;          // 응답 데이터 (제네릭 타입)

    // 성공 응답 생성
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "요청에 성공하였습니다.", data);
    }

    // 성공 응답 생성 (커스텀 메시지)
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    // 실패 응답 생성
    public static <T> ApiResponse<T> failure(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
