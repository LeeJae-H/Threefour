package com.threefour.user.application;

import com.threefour.common.ErrorCode;
import com.threefour.common.ExpectedException;
import com.threefour.user.domain.User;
import com.threefour.user.domain.UserRepository;
import com.threefour.user.dto.EmailValidationRequest;
import com.threefour.user.dto.JoinRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class UserJoinService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailSender mailSender;
    // todo 추후 분산 서버를 고려한다면 Redis로 변경해야 합니다.
    private final Map<String, String> emailValidationCache = new ConcurrentHashMap<>();

    @Transactional
    public String join(JoinRequest joinRequest) {
        String email = joinRequest.getEmail();
        String password = joinRequest.getPassword();
        String nickname = joinRequest.getNickname();

        validateEmail(email);
        validatePassword(password);
        validateNickname(nickname);

        // 비밀번호는 암호화한 후 전달
        User newUser = User.join(email, passwordEncoder.encode(password), nickname);
        User savedUser = userRepository.save(newUser);
        return savedUser.getNickname();
    }

    public void sendEmailAuthNumberForJoin(String email) {
        validateEmail(email);

        // todo 사용자 경험을 위해 메일 발송 자체는 이벤트(도메인 이벤트)로 처리
        int authNumber = mailSender.sendMail(email);
        emailValidationCache.put(email, String.valueOf(authNumber));
    }

    public void validateEmailForJoin(EmailValidationRequest emailValidationRequest) {
        String email = emailValidationRequest.getEmail();
        String authNumber = emailValidationRequest.getAuthNumber();

        String storedAuthNumber = emailValidationCache.get(email);
        if (storedAuthNumber == null || !storedAuthNumber.equals(authNumber)) {
            throw new ExpectedException(ErrorCode.FAIL_VALIDATE_EMAIL);
        }
        emailValidationCache.remove(emailValidationRequest.getEmail());
    }

    public void validateNicknameForJoin(String nickname) {
        validateNickname(nickname);
    }

    // 이메일은 고유하다.
    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ExpectedException(ErrorCode.ALREADY_USED_EMAIL);
        }
    }

    // 비밀번호는 최소 8자 이상이어야 하며, 공백을 포함할 수 없다.
    private void validatePassword(String password) {
        if (password == null || password.contains(" ") || password.length() < 8) {
            throw new ExpectedException(ErrorCode.INVALID_PASSWORD_LENGTH);
        }
    }

    // 닉네임은 (양쪽 끝의 공백 제거 후) 2~10자 이내여야 하며, 특수문자를 포함할 수 없다. 또한, 닉네임은 고유하다.
    private void validateNickname(String nickname) {
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
