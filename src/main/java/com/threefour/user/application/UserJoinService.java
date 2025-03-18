package com.threefour.user.application;

import com.threefour.common.ErrorCode;
import com.threefour.common.ExpectedException;
import com.threefour.user.domain.MailSender;
import com.threefour.user.domain.PasswordEncoder;
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

    private final PasswordEncoder passwordEncoder;
    private final UserInfoValidator userInfoValidator;
    private final MailSender mailSender;
    private final UserRepository userRepository;
    // todo 추후 분산 서버를 고려한다면 Redis로 변경해야 합니다.
    private final Map<String, String> emailValidationCache = new ConcurrentHashMap<>();

    @Transactional
    public void join(JoinRequest joinRequest) {
        String email = joinRequest.getEmail();
        String password = joinRequest.getPassword();
        String nickname = joinRequest.getNickname();

        userInfoValidator.validateEmail(email);
        userInfoValidator.validatePassword(password);
        userInfoValidator.validateNickname(nickname);

        // 비밀번호는 암호화한 후 전달
        User newUser = User.join(email, passwordEncoder.encode(password), nickname);
        userRepository.save(newUser);
    }

    public void sendEmailAuthNumber(String email) {
        userInfoValidator.validateEmail(email);

        // todo 사용자 경험을 위해 메일 발송 자체는 이벤트(도메인 이벤트)로 처리
        int authNumber = mailSender.sendMail(email);
        emailValidationCache.put(email, String.valueOf(authNumber));
    }

    public void validateEmailAuthNumber(EmailValidationRequest emailValidationRequest) {
        String email = emailValidationRequest.getEmail();
        String authNumber = emailValidationRequest.getAuthNumber();

        String storedAuthNumber = emailValidationCache.get(email);
        if (storedAuthNumber == null || !storedAuthNumber.equals(authNumber)) {
            throw new ExpectedException(ErrorCode.FAIL_VALIDATE_EMAIL);
        }
        emailValidationCache.remove(emailValidationRequest.getEmail());
    }

    public void validateNickname(String nickname) {
        userInfoValidator.validateNickname(nickname);
    }
}
