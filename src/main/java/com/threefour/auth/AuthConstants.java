package com.threefour.auth;

public class AuthConstants {

    public static final String[] WHITELIST_URLS = {"/login", "/reissue", "/join"};
    public static final Long ACCESS_TOKEN_EXPIRATION_TIME = 60*60*10L;
    public static final Long REFRESH_TOKEN_EXPIRATION_TIME = 60*60*60*24L;
}