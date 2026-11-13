package com.mysite.sbb.global.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

// 메일 발송. spring.mail.host 설정이 있으면 Spring Boot가 JavaMailSender를 만들어 준다.
// 설정이 없으면(개발 환경) 실제로 보내지 않고 로그로만 남긴다.
// SMTP 계정과 비밀번호는 저장소에 올리지 않는다. -> application-secret.yaml(.gitignore) 또는 환경변수
@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    public void send(String to, String subject, String text) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();

        if (mailSender == null) {
            log.warn("[메일 미설정: 발송 생략] to={}, subject={}\n{}", to, subject, text);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
