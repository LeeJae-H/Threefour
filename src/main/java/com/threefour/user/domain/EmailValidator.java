package com.threefour.user.domain;

public interface EmailValidator {

    void sendEmailAuthNumber(String email);
    void validateEmailAuthNumber(String email, String authNumber);
}