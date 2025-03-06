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

    @ExceptionHandler(ExpectedException.class)
    public ResponseEntity<ApiResponse<?>> handleExpectedException(ExpectedException ex) {
        ErrorCode errorCode = ex.getErrorCode();

        String errorMessage = errorCode.getMessage();
        HttpStatus status = errorCode.getHttpStatus();
        return ApiResponse.error(errorMessage, status);
    }

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleUnExpectedException(Exception ex) {
        log.error("에러 발생 :", ex);

        String errorMessage = ErrorCode.INTERNAL_SERVER_ERROR.getMessage(ex.getMessage());
        HttpStatus status = ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus();
        return ApiResponse.error(errorMessage, status);
    }
}