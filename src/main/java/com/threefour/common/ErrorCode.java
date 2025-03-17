package com.threefour.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "00", "서버 내부 오류가 발생했습니다 : %s"),

    // Jwt
    INVALID_REFRESH_TOKEN_FORMAT(HttpStatus.BAD_REQUEST, "00", "유효하지 않은 리포레시 토큰 형식입니다."),
    INVALID_REFRESH_TOKEN_TYPE(HttpStatus.BAD_REQUEST, "00", "리프레시 토큰이 아닙니다."),
    REFRESH_TOKEN_IS_EXPIRED(HttpStatus.BAD_REQUEST, "00", "만료된 리프레시 토큰입니다."),
    REFRESH_TOKEN_NOT_EXISTS_DATABASE(HttpStatus.BAD_REQUEST, "00", "리프레시 토큰이 데이터베이스에 저장되어 있지 않습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "00", "유효하지 않은 엑세스 토큰입니다."),

    // Mail
    FAIL_SEND_MAIL(HttpStatus.BAD_REQUEST, "00", "이메일 발송에 실패했습니다."),
    FAIL_VALIDATE_MAIL(HttpStatus.BAD_REQUEST, "00", "이메일 인증에 실패했습니다."),

    // 회원
    INVALID_PASSWORD_LENGTH(HttpStatus.BAD_REQUEST, "00", "비밀번호는 최소 8자 이상이어야 합니다."),
    INVALID_NICKNAME_LENGTH(HttpStatus.BAD_REQUEST, "00", "닉네임은 2~10자 이내여야 합니다."),
    INVALID_NICKNAME_FORMAT(HttpStatus.BAD_REQUEST, "00", "닉네임은 특수문자를 포함할 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "00", "사용자를 찾을 수 없습니다."),
    ALREADY_USED_EMAIL(HttpStatus.BAD_REQUEST, "00", "이미 사용 중인 이메일입니다."),
    ALREADY_USED_NICKNAME(HttpStatus.BAD_REQUEST, "00", "이미 사용 중인 닉네임입니다."),
    USER_ACCOUNT_ACCESS_DENIED(HttpStatus.BAD_REQUEST, "00", "회원 계정 접근 권한이 없습니다."),

    // 게시글
    POST_NOT_FOUND(HttpStatus.BAD_REQUEST, "00", "게시글을 찾을 수 없습니다."),
    POST_ACCESS_DENIED(HttpStatus.BAD_REQUEST, "00", "게시글 접근 권한이 없습니다."),
    INVALID_TITLE_LENGTH(HttpStatus.BAD_REQUEST, "00", "게시글 제목은 1~50자 이내여야 합니다."),
    INVALID_CONTENT_LENGTH(HttpStatus.BAD_REQUEST, "00", "게시글 내용은 최소 1자 이상이어야 합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}
