package com.threefour.auth;

public class AuthConstants {

    public static final String[] WHITELIST_URLS = {
            // 화면
            "/home", "/home/*", "/users/join", "/users/my/info", "/posts/*", "/posts/edit/*", "/posts/category/**", "/posts/write/*",
            // 회원가입
            "/api/users/join", "/api/users/join/send-email", "/api/users/join/validate-nickname", "/api/users/join/validate-email",
            // 토큰
            "/api/token/reissue", "/api/token/validate",
            // 게시글
            "/api/posts", "/api/posts/id/*", "/api/posts/category/*"
    };
    public static final Long ACCESS_TOKEN_EXPIRATION_TIME = 1000*60*10L; // 10분
    public static final Long REFRESH_TOKEN_EXPIRATION_TIME = 1000*60*60*24L; // 24시간
}