package com.threefour.auth;

public class AuthConstants {

    public static final String[] WHITELIST_URLS = {"/home",
            "/api/users/join", "/users/join", "/api/users/send-email", "/api/users/validate-nickname", "/api/users/validate-email",
            "/users/my-info", "/login", "/api/token/reissue", "/api/token/validate"};
    public static final Long ACCESS_TOKEN_EXPIRATION_TIME = 1000*60*10L; // 10분
    public static final Long REFRESH_TOKEN_EXPIRATION_TIME = 1000*60*60*24L; // 24시간
}