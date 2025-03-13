package com.threefour.user.infrastructure;

import com.threefour.common.ErrorCode;
import com.threefour.common.ExpectedException;
import com.threefour.user.application.MailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailSenderImpl implements MailSender {

    private final JavaMailSender mailSender;
    private static int authNumber;
    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public int sendMail(String email) {
        MimeMessage message = createMessage(email);
        mailSender.send(message);
        return authNumber;
    }

    private int createNumber() {
        return (int)(Math.random() * (90000)) + 100000; // 6자리 숫자
    }

    private MimeMessage createMessage(String mail) {
        authNumber = createNumber();

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
            throw new ExpectedException(ErrorCode.FAIL_SEND_MAIL);
        }

        return message;
    }
}
