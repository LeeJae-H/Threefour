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

        String errorMessage = errorCode.getMessage();
        HttpStatus status = errorCode.getHttpStatus();
        return ApiResponse.error(errorMessage, status);
    }

    /**
     * Validation 관련 예외가 발생했을 때 동작합니다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append("[");
            builder.append(fieldError.getField());
            builder.append("](은)는 ");
            builder.append(fieldError.getDefaultMessage());
            builder.append(" 입력된 값: [");
            builder.append(fieldError.getRejectedValue());
            builder.append("]");
        }

        String errorMessage = ErrorCode.FAIL_VALIDATION.getMessage(builder.toString());
        HttpStatus status = ErrorCode.FAIL_VALIDATION.getHttpStatus();
        return ApiResponse.error(errorMessage, status);
    }

    /**
     * 예상하지 못한 예외가 발생했을 때 동작합니다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleUnExpectedException(Exception ex) {
        log.error("에러 발생 :", ex);

        String errorMessage = ErrorCode.INTERNAL_SERVER_ERROR.getMessage(ex.getMessage());
        HttpStatus status = ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus();
        return ApiResponse.error(errorMessage, status);
    }
}