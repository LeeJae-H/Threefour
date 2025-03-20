package com.threefour.application.user;

import com.threefour.domain.user.EmailValidator;
import com.threefour.domain.user.PasswordEncoder;
import com.threefour.domain.user.User;
import com.threefour.domain.user.UserRepository;
import com.threefour.dto.user.EmailAuthNumberRequest;
import com.threefour.dto.user.JoinRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserJoinService {

    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;
    private final EmailValidator emailValidator;
    private final UserRepository userRepository;

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

        // 회원가입
        User newUser = User.join(email, encodedPassword, nickname);
        userRepository.save(newUser);
    }

    public void sendEmailAuthNumber(String email) {
        // 값 검증
        userValidator.validateEmail(email);

        // 이메일 인증번호 발송
        emailValidator.sendEmailAuthNumber(email);
    }

    public void validateEmailAuthNumber(EmailAuthNumberRequest emailAuthNumberRequest) {
        String email = emailAuthNumberRequest.getEmail();
        String authNumber = emailAuthNumberRequest.getAuthNumber();

        // 이메일 인증번호 확인
        emailValidator.validateEmailAuthNumber(email, authNumber);
    }

    public void validateNickname(String nickname) {
        // 값 검증
        userValidator.validateNickname(nickname);
    }
}
