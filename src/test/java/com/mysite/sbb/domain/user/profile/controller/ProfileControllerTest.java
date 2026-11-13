package com.mysite.sbb.domain.user.profile.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProfileControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("user2 프로필: 이메일, 활동 수, 최근 답변 링크")
    @WithUserDetails("user2")
    void t1() throws Exception {
        mvc.perform(get("/user/profile"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("user2@test.com")))
                .andExpect(content().string(containsString("질문 0 · 답변 1 · 댓글 0")))
                .andExpect(content().string(containsString("/question/detail/2#answer_1")));
    }

    @Test
    @DisplayName("비로그인 -> 로그인 페이지로")
    void t2() throws Exception {
        mvc.perform(get("/user/profile"))
                .andExpect(status().isFound());
    }
}
