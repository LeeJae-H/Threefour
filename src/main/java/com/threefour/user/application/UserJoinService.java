package com.threefour.user.application;

import com.threefour.common.ErrorCode;
import com.threefour.common.ExpectedException;
import com.threefour.user.domain.MailSender;
import com.threefour.user.domain.PasswordEncoder;
import com.threefour.user.domain.User;
import com.threefour.user.domain.UserRepository;
import com.threefour.user.dto.EmailAuthNumberRequest;
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
    private final UserValidator userValidator;
    private final MailSender mailSender;
    private final UserRepository userRepository;
    // todo 추후 분산 서버를 고려한다면 Redis로 변경해야 합니다.
    private final Map<String, String> emailValidationCache = new ConcurrentHashMap<>();

    @Transactional
    public void join(JoinRequest joinRequest) {
        String email = joinRequest.getEmail();
        String password = joinRequest.getPassword();
        String nickname = joinRequest.getNickname();

        // 값 검증
        userValidator.validateEmail(email);
        userValidator.validatePassword(password);
        userValidator.validateNickname(nickname);

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        User newUser = User.join(email, encodedPassword, nickname);
        userRepository.save(newUser);
    }

    public void sendEmailAuthNumber(String email) {
        // 값 검증
        userValidator.validateEmail(email);

        // todo 사용자 경험을 위해 메일 발송 자체는 이벤트(도메인 이벤트)로 처리
        int authNumber = mailSender.sendMail(email);
        emailValidationCache.put(email, String.valueOf(authNumber));
    }

    public void validateEmailAuthNumber(EmailAuthNumberRequest emailAuthNumberRequest) {
        String email = emailAuthNumberRequest.getEmail();
        String authNumber = emailAuthNumberRequest.getAuthNumber();

        String storedAuthNumber = emailValidationCache.get(email);
        if (storedAuthNumber == null || !storedAuthNumber.equals(authNumber)) {
            throw new ExpectedException(ErrorCode.FAIL_VALIDATE_EMAIL);
        }
        emailValidationCache.remove(emailAuthNumberRequest.getEmail());
    }

    public void validateNickname(String nickname) {
        // 값 검증
        userValidator.validateNickname(nickname);
    }
}
