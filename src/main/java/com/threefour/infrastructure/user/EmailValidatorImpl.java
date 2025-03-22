package com.threefour.infrastructure.user;

import com.threefour.common.ErrorCode;
import com.threefour.common.ExpectedException;
import com.threefour.domain.user.EmailValidator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class EmailValidatorImpl implements EmailValidator {

    private final JavaMailSender mailSender;
    // todo 추후 분산 서버를 고려한다면 Redis로 변경해야 합니다.
    private final Map<String, String> emailValidationCache = new ConcurrentHashMap<>();

    private static String authNumber;

    @Value("${spring.mail.username}")
    private String sender;

    @Async
    @Override
    public void sendEmailAuthNumber(String email) {
        // 인증번호 생성
        authNumber = createNumber();
        // 이메일 메세지 생성
        MimeMessage message = createMessage(email);
        // 이메일 인증번호 발송
        mailSender.send(message);
        // cache에 저장
        emailValidationCache.put(email, String.valueOf(authNumber));
    }

    @Override
    public void validateEmailAuthNumber(String email, String authNumber) {
        String storedAuthNumber = emailValidationCache.get(email);
        if (storedAuthNumber == null || !storedAuthNumber.equals(authNumber)) {
            throw new ExpectedException(ErrorCode.FAIL_VALIDATE_EMAIL);
        }
        emailValidationCache.remove(email);
    }

    private String createNumber() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }

    private MimeMessage createMessage(String mail) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            message.setFrom(sender);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("쓰리포");
            String body = "";
            body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
            body += "<h1>" + authNumber + "</h1>";
            message.setText(body,"UTF-8", "html");
        } catch (MessagingException e) {
            throw new ExpectedException(ErrorCode.FAIL_SEND_EMAIL);
        }
        return message;
    }
}
