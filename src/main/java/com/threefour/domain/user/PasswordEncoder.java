package com.threefour.domain.user;

public interface PasswordEncoder {

    String encode(String rawPassword);
}
