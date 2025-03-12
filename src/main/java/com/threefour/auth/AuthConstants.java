package com.threefour.auth;

public class AuthConstants {

    public static final String[] WHITELIST_URLS = {"/login", "/reissue", "/users/join", "/home"};
    public static final Long ACCESS_TOKEN_EXPIRATION_TIME = 1000*60*10L; // 10분
    public static final Long REFRESH_TOKEN_EXPIRATION_TIME = 1000*60*60*24L; // 24시간
}