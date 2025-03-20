package com.threefour.common;

public enum ErrorCode {

    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다 : %s"),

    // 인증/인가(Auth)
    NOT_ACCESS_TOKEN(401, "NOT_ACCESS_TOKEN", "엑세스 토큰이 아닙니다."),
    NOT_REFRESH_TOKEN(401, "NOT_REFRESH_TOKEN", "리프레시 토큰이 아닙니다."),
    EXPIRED_ACCESS_TOKEN(401, "EXPIRED_ACCESS_TOKEN", "만료된 엑세스 토큰입니다."),
    EXPIRED_REFRESH_TOKEN(401, "EXPIRED_REFRESH_TOKEN", "만료된 리프레시 토큰입니다."),

    // 회원(User)
    ALREADY_USED_EMAIL(400, "ALREADY_USED_EMAIL", "이미 사용 중인 이메일입니다."),
    ALREADY_USED_NICKNAME(400, "ALREADY_USED_NICKNAME", "이미 사용 중인 닉네임입니다."),
    INVALID_EMAIL_FORMAT(400, "INVALID_EMAIL_FORMAT", "올바른 이메일 형식이 아닙니다."),
    INVALID_PASSWORD_LENGTH(400, "INVALID_PASSWORD_LENGTH", "비밀번호는 최소 8자 이상이어야 합니다."),
    INVALID_NICKNAME_LENGTH(400, "INVALID_NICKNAME_LENGTH", "닉네임은 2~10자 이내여야 합니다."),
    INVALID_NICKNAME_FORMAT(400, "INVALID_NICKNAME_FORMAT", "닉네임은 특수문자를 포함할 수 없습니다."),
    FAIL_SEND_EMAIL(400, "FAIL_SEND_EMAIL", "이메일 인증번호 발송에 실패했습니다."),
    FAIL_VALIDATE_EMAIL(400, "FAIL_VALIDATE_EMAIL", "이메일 인증에 실패했습니다."),
    USER_NOT_FOUND(400, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),

    // 게시글(Post)
    INVALID_TITLE_LENGTH(400, "INVALID_TITLE_LENGTH", "게시글 제목은 1~50자 이내여야 합니다."),
    INVALID_CONTENT_LENGTH(400, "INVALID_CONTENT_LENGTH", "게시글 내용은 최소 1자 이상이어야 합니다."),
    POST_NOT_FOUND(400, "POST_NOT_FOUND", "게시글을 찾을 수 없습니다."),
    POST_ACCESS_DENIED(400, "POST_ACCESS_DENIED", "게시글 접근 권한이 없습니다.");

    private final int httpStatus;
    private final String code;
    private final String message;

    ErrorCode(int httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}
