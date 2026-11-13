package com.mysite.sbb.global.mail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
class MailServiceTest {
    @Autowired
    private MailService mailService;
    @Autowired
    private ApplicationContext context;

    @Test
    @DisplayName("test 프로파일은 비밀 설정을 읽지 않으므로 JavaMailSender 빈이 없다")
    void t1() {
        assertThat(context.getBeanProvider(JavaMailSender.class).getIfAvailable()).isNull();
    }

    @Test
    @DisplayName("메일 설정이 없으면 예외 없이 로그로만 남긴다")
    void t2() {
        assertThatCode(() -> mailService.send("user1@test.com", "제목", "내용")).doesNotThrowAnyException();
    }
}
