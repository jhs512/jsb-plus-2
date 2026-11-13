package com.mysite.sbb.domain.user.user.service;

import com.mysite.sbb.domain.user.user.entity.SiteUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.mysite.sbb.domain.user.user.repository.UserRepository;
import com.mysite.sbb.global.mail.MailService;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    // 진짜 메일 대신 가짜(Mock)를 넣고, 어떤 내용으로 호출되었는지 확인한다.
    @MockitoBean
    private MailService mailService;

    @Test
    @DisplayName("회원 생성: 비밀번호는 암호화되어 저장된다")
    void t1() {
        SiteUser user = userService.create("newuser", "newuser@test.com", "1234");

        assertThat(user.getId()).isGreaterThan(0);
        assertThat(user.getPassword()).isNotEqualTo("1234").startsWith("$2a$"); // BCrypt 형식
        assertThat(passwordEncoder.matches("1234", user.getPassword())).isTrue();
        assertThat(userService.existsByUsername("newuser")).isTrue();
    }

    @Test
    @DisplayName("비밀번호 찾기: 임시 비밀번호로 바뀌고, 그 비밀번호가 메일로 발송된다")
    void t2() {
        userService.resetPassword("user1@test.com");

        ArgumentCaptor<String> text = ArgumentCaptor.forClass(String.class);
        verify(mailService).send(eq("user1@test.com"), anyString(), text.capture());

        String tempPassword = text.getValue().replaceAll(".*임시 비밀번호는 (\\S+) 입니다.*", "$1");
        SiteUser user1 = userRepository.findByUsername("user1").get();
        assertThat(passwordEncoder.matches("1234", user1.getPassword())).isFalse();
        assertThat(passwordEncoder.matches(tempPassword, user1.getPassword())).isTrue();
    }

    @Test
    @DisplayName("비밀번호 찾기: 없는 이메일이면 아무 일도 하지 않는다(예외 없음)")
    void t3() {
        userService.resetPassword("nobody@test.com");

        verify(mailService, never()).send(anyString(), anyString(), anyString());
    }
}
