package com.threefour.auth;

public class AuthConstants {

    public static final String[] WHITELIST_URLS = {
            // JWT
            "/api/token/reissue",
            // 화면(HTML)
            "/view/**",
            // 회원(User)
            "/api/users/join", "/api/users/join/*",
            // 게시글(Post)
            "/api/posts/*/details", "/api/posts/all", "/api/posts/all/*"
    };
    public static final Long ACCESS_TOKEN_EXPIRATION_TIME = 1000*60*10L; // 10분
    public static final Long REFRESH_TOKEN_EXPIRATION_TIME = 1000*60*60*24L; // 24시간
}