package com.threefour.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * ExpectedException 예외가 발생했을 때 동작합니다.
     */
    @ExceptionHandler(ExpectedException.class)
    public ResponseEntity<ApiResponse<?>> handleExpectedException(ExpectedException ex) {
        ErrorCode errorCode = ex.getErrorCode();

        String code = errorCode.getCode();
        String message = errorCode.getMessage();
        HttpStatus status = errorCode.getHttpStatus();

        return ApiResponse.error(code, message, status);
    }

    /**
     * 예상하지 못한 예외가 발생했을 때 동작합니다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleUnExpectedException(Exception ex) {
        log.error("서버 내부 오류 발생 :", ex);

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        String code = errorCode.getCode();
        String message = errorCode.getMessage(ex.getMessage());
        HttpStatus status = errorCode.getHttpStatus();

        return ApiResponse.error(code, message, status);
    }
}