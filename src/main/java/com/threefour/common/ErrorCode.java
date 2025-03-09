package com.threefour.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 예상하지 못한 예외
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다 : %s"),

    // Validation
    FAIL_VALIDATION(HttpStatus.BAD_REQUEST, "잘못된 입력 : %s"),

    // Jwt
    INVALID_REFRESH_TOKEN_FORMAT(HttpStatus.BAD_REQUEST, "유효하지 않은 리포레시 토큰 형식입니다."),
    INVALID_REFRESH_TOKEN_TYPE(HttpStatus.BAD_REQUEST, "리프레시 토큰이 아닙니다."),
    REFRESH_TOKEN_IS_EXPIRED(HttpStatus.BAD_REQUEST, "만료된 리프레시 토큰입니다."),

    // 회원
    INVALID_PASSWORD_LENGTH(HttpStatus.BAD_REQUEST, "비밀번호는 최소 8자 이상이어야 합니다."),
    INVALID_NICKNAME_LENGTH(HttpStatus.BAD_REQUEST, "닉네임은 2~10자 이내여야 합니다."),
    INVALID_NICKNAME_FORMAT(HttpStatus.BAD_REQUEST, "닉네임은 특수문자를 포함할 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다."),
    ALREADY_EXIST_USER(HttpStatus.BAD_REQUEST, "이미 존재하는 사용자입니다.");


    private final HttpStatus httpStatus;
    private final String message;

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}
