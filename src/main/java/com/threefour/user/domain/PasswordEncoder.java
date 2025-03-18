package com.threefour.user.domain;

public interface PasswordEncoder {

    String encode(String rawPassword);
}
