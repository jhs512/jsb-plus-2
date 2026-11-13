package com.mysite.sbb.domain.question.question.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class QuestionControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("GET /question/list -> 질문 제목들이 보인다")
    void t1() throws Exception {
        mvc.perform(get("/question/list"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("sbb가 무엇인가요?")))
                .andExpect(content().string(containsString("스프링부트 모델 질문입니다.")));
    }
}
