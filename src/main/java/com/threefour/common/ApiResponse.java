package com.threefour.common;

public class ApiResponse<T> {

    private final boolean success;
    private final T data;               // 실패 시 null
    private final String errorCode;     // 성공 시 null
    private final String errorMessage;  // 성공 시 null

    private ApiResponse(boolean success, T data, String errorCode, String errorMessage) {
        this.success = success;
        this.data = data;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * 성공 응답 객체를 생성할 때 사용합니다.
     *
     * @param data
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null);
    }

    /**
     * 예외 응답 객체를 생성할 때 사용합니다.
     *
     * GlobalExceptionHandler에서 사용됩니다.
     * CustomAuthenticationEntryPoint에서 사용됩니다.
     *
     * @param errorCode
     * @param errorMessage
     */
    public static ApiResponse<?> error(String errorCode, String errorMessage) {
        return new ApiResponse<>(false, null, errorCode, errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}