package com.threefour.application.user;

import com.threefour.common.ErrorCode;
import com.threefour.common.ExpectedException;
import com.threefour.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    // 이메일은 올바른 형식이어야 한다. 또한, 이메일은 고유하다.
    public void validateEmail(String email) {
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new ExpectedException(ErrorCode.INVALID_EMAIL_FORMAT);
        }
        if (userRepository.existsByEmail(email)) {
            throw new ExpectedException(ErrorCode.ALREADY_USED_EMAIL);
        }
    }

    // 비밀번호는 최소 8자 이상이어야 하며, 공백을 포함할 수 없다.
    public void validatePassword(String password) {
        if (password == null || password.contains(" ") || password.length() < 8) {
            throw new ExpectedException(ErrorCode.INVALID_PASSWORD_LENGTH);
        }
    }

    // 닉네임은 (양쪽 끝의 공백 제거 후) 2~10자 이내여야 하며, 특수문자를 포함할 수 없다. 또한, 닉네임은 고유하다.
    public void validateNickname(String nickname) {
        if (nickname == null) {
            throw new ExpectedException(ErrorCode.INVALID_NICKNAME_LENGTH);
        }
        String trimmedNickname = nickname.trim();
        if (trimmedNickname.length() < 2 || trimmedNickname.length() > 10) {
            throw new ExpectedException(ErrorCode.INVALID_NICKNAME_LENGTH);
        }
        if (!trimmedNickname.matches("^[a-zA-Z0-9가-힣]+$")) {
            throw new ExpectedException(ErrorCode.INVALID_NICKNAME_FORMAT);
        }
        if (userRepository.existsByNickname(trimmedNickname)) {
            throw new ExpectedException(ErrorCode.ALREADY_USED_NICKNAME);
        }
    }
}