package com.mysite.sbb.domain.user.user.service;

import com.mysite.sbb.domain.user.user.entity.SiteUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원 생성: 비밀번호는 암호화되어 저장된다")
    void t1() {
        SiteUser user = userService.create("newuser", "newuser@test.com", "1234");

        assertThat(user.getId()).isGreaterThan(0);
        assertThat(user.getPassword()).isNotEqualTo("1234").startsWith("$2a$"); // BCrypt 형식
        assertThat(passwordEncoder.matches("1234", user.getPassword())).isTrue();
        assertThat(userService.existsByUsername("newuser")).isTrue();
    }
}
