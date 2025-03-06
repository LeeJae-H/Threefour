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

    // RestController 응답에서 사용합니다.
    // 성공 응답 - 200 상태코드의 경우 사용합니다.
    // Body와 Header 모두에 Http 상태 코드를 담습니다.
    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return new ResponseEntity<>(new ApiResponse<>(true, HttpStatus.OK.value(), data, null), HttpStatus.OK);
    }

    // RestController 응답에서 사용합니다.
    // 성공 응답 - 200이 아닌 상태코드의 경우 사용하며, 상태코드를 파라미터로 전달 받습니다.
    // Body와 Header 모두에 Http 상태 코드를 담습니다.
    public static <T> ResponseEntity<ApiResponse<T>> success(T data, HttpStatus status) {
        return new ResponseEntity<>(new ApiResponse<>(true, status.value(), data, null), status);
    }

    // GlobalExceptionHandler에서 사용됩니다.
    // Body와 Header 모두에 Http 상태 코드를 담습니다.
    public static <T> ResponseEntity<ApiResponse<?>> error(String errorMessage, HttpStatus status) {
        return new ResponseEntity<>(new ApiResponse<>(false, status.value(), null, errorMessage), status);
    }
}
