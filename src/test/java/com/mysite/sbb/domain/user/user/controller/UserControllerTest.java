package com.mysite.sbb.domain.user.user.controller;

import com.mysite.sbb.domain.user.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockHttpServletRequestBuilder signup(String username, String pw1, String pw2, String email) {
        return post("/user/signup").with(csrf())
                .param("username", username)
                .param("password1", pw1)
                .param("password2", pw2)
                .param("email", email);
    }

    @Test
    @DisplayName("회원가입 성공 -> 메인으로 리다이렉트")
    void t1() throws Exception {
        mvc.perform(signup("newuser", "1234", "1234", "newuser@test.com"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"));

        assertThat(userRepository.findByUsername("newuser")).isPresent();
    }

    @Test
    @DisplayName("비밀번호 확인 불일치 -> password2 에러")
    void t2() throws Exception {
        mvc.perform(signup("newuser", "1234", "4321", "newuser@test.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup_form"))
                .andExpect(model().attributeHasFieldErrorCode("form", "password2", "passwordInCorrect"));
    }

    @Test
    @DisplayName("이미 있는 사용자ID -> username 에러")
    void t3() throws Exception {
        mvc.perform(signup("dupuser", "1234", "1234", "dup1@test.com"));
        mvc.perform(signup("dupuser", "1234", "1234", "dup2@test.com"))
                .andExpect(view().name("signup_form"))
                .andExpect(model().attributeHasFieldErrorCode("form", "username", "duplicate"));
    }

    @Test
    @DisplayName("이메일 형식 오류 -> email 에러")
    void t4() throws Exception {
        mvc.perform(signup("newuser", "1234", "1234", "not-email"))
                .andExpect(view().name("signup_form"))
                .andExpect(model().attributeHasFieldErrors("form", "email"));
    }

    @Test
    @DisplayName("로그인 성공 -> 인증됨, / 로 이동")
    void t5() throws Exception {
        mvc.perform(formLogin("/user/login").user("user1").password("1234"))
                .andExpect(authenticated().withUsername("user1").withRoles("USER"))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("admin 계정은 ADMIN 권한")
    void t6() throws Exception {
        mvc.perform(formLogin("/user/login").user("admin").password("1234"))
                .andExpect(authenticated().withRoles("ADMIN"));
    }

    @Test
    @DisplayName("비밀번호 틀림 -> 인증 안 됨, /user/login?error")
    void t7() throws Exception {
        mvc.perform(formLogin("/user/login").user("user1").password("wrong"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/user/login?error"));
    }

    @Test
    @DisplayName("로그아웃 -> / 로 이동")
    void t8() throws Exception {
        mvc.perform(logout("/user/logout"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("비밀번호 변경: 현재 비밀번호가 맞으면 변경")
    @WithUserDetails("user1")
    void t9() throws Exception {
        mvc.perform(put("/user/password").with(csrf())
                        .param("currentPassword", "1234")
                        .param("newPassword1", "5678")
                        .param("newPassword2", "5678"))
                .andExpect(redirectedUrl("/user/password"));

        assertThat(passwordEncoder.matches("5678", userRepository.findByUsername("user1").get().getPassword())).isTrue();
    }

    @Test
    @DisplayName("비밀번호 변경: 현재 비밀번호가 틀리면 에러")
    @WithUserDetails("user1")
    void t10() throws Exception {
        mvc.perform(put("/user/password").with(csrf())
                        .param("currentPassword", "wrong")
                        .param("newPassword1", "5678")
                        .param("newPassword2", "5678"))
                .andExpect(view().name("password_form"))
                .andExpect(model().attributeHasFieldErrorCode("form", "currentPassword", "mismatch"));
    }

    @Test
    @DisplayName("소셜 로그인 설정이 없으면 로그인 화면에 소셜 버튼이 없다")
    void t11() throws Exception {
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/user/login"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("href=\"/oauth2/authorization"))));  // 주석 속 설명 문구는 제외
    }
}
