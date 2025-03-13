package com.threefour.user.application;

public interface PasswordEncoder {

    String encode(String rawPassword);
}
