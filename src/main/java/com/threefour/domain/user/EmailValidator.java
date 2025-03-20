package com.threefour.domain.user;

public interface EmailValidator {

    void sendEmailAuthNumber(String email);
    void validateEmailAuthNumber(String email, String authNumber);
}