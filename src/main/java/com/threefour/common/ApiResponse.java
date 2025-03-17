package com.threefour.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    private final boolean success;
    private final T data;               // 실패 시 null
    @JsonProperty("code")
    private final String errorCode;     // 성공 시 null
    @JsonProperty("message")
    private final String errorMessage;  // 성공 시 null

    /**
     * 200 상태 코드의 성공 응답 객체를 생성할 때 사용합니다.
     *
     * @param data
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return new ResponseEntity<>(new ApiResponse<>(true, data, null, null), HttpStatus.OK);
    }

    /**
     * 성공 응답 객체를 생성할 때 사용합니다.
     *
     * @param data
     * @param status
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(T data, HttpStatus status) {
        return new ResponseEntity<>(new ApiResponse<>(true, data, null, null), status);
    }

    /**
     * 예외 응답 객체를 생성할 때 사용합니다.
     * GlobalExceptionHandler에서 사용됩니다.
     *
     * @param errorCode
     * @param errorMessage
     * @param status
     */
    public static ResponseEntity<ApiResponse<?>> error(String errorCode, String errorMessage, HttpStatus status) {
        return new ResponseEntity<>(new ApiResponse<>(false, null, errorCode, errorMessage), status);
    }


    /**
     * 예외 응답 객체를 생성할 때 사용합니다.
     * CustomAuthenticationEntryPoint에서 사용됩니다.
     *
     * @param errorCode
     * @param errorMessage
     */
    public static ApiResponse<?> error(String errorCode, String errorMessage) {
        return new ApiResponse<>(false, null, errorCode, errorMessage);
    }
}