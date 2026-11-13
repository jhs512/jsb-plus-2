package com.mysite.sbb.domain.user.user.controller;

import com.mysite.sbb.domain.user.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}
