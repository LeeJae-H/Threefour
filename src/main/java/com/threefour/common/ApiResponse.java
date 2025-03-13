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
    private final int status;
    private final T data;
    @JsonProperty("message") // Json으로 직렬화할 때, message 필드명으로 전달
    private final String errorMessage;

    /**
     * ApiResponse 객체를 생성합니다.
     *
     * RestController에서 성공 응답 객체를 생성할 때 사용하며, 200 상태코드일 때 사용합니다.
     * HTTP Response의 body와 header 모두에 HTTP 상태 코드를 담습니다.
     * @param data
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return new ResponseEntity<>(new ApiResponse<>(true, HttpStatus.OK.value(), data, null), HttpStatus.OK);
    }

    /**
     * ApiResponse 객체를 생성합니다.
     *
     * RestController에서 성공 응답 객체를 생성할 때 사용하며, 200이 아닌 상태코드일 때 사용합니다.
     * HTTP Response의 body와 header 모두에 HTTP 상태 코드를 담습니다.
     * @param data
     * @param status
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(T data, HttpStatus status) {
        return new ResponseEntity<>(new ApiResponse<>(true, status.value(), data, null), status);
    }

    /**
     * ApiResponse 객체를 생성합니다.
     *
     * RestController에서 실패 응답 객체를 생성할 때 사용하며, 400 상태코드일 때 사용합니다.
     * HTTP Response의 body와 header 모두에 HTTP 상태 코드를 담습니다.
     * @param data
     */
    public static <T> ResponseEntity<ApiResponse<T>> fail(T data) {
        return new ResponseEntity<>(new ApiResponse<>(true, HttpStatus.BAD_REQUEST.value(), data, null), HttpStatus.BAD_REQUEST);
    }

    /**
     * ApiResponse 객체를 생성합니다.
     *
     * GlobalExceptionHandler에서 예외 응답 객체를 생성할 때 사용합니다.
     * HTTP Response의 body와 header 모두에 HTTP 상태 코드를 담습니다.
     * @param errorMessage
     * @param status
     */
    public static <T> ResponseEntity<ApiResponse<?>> error(String errorMessage, HttpStatus status) {
        return new ResponseEntity<>(new ApiResponse<>(false, status.value(), null, errorMessage), status);
    }
}
