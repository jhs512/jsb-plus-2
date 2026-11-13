package com.mysite.sbb.domain.question.question.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // 테스트에서 등록한 질문은 롤백
class QuestionControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("GET /question/list -> 질문 제목들이 보인다")
    void t1() throws Exception {
        mvc.perform(get("/question/list"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("sbb가 무엇인가요?")))
                .andExpect(content().string(containsString("스프링부트 모델 질문입니다.")))
                .andExpect(content().string(containsString("href=\"/question/detail/2\"")));
    }

    @Test
    @DisplayName("GET /question/detail/2 -> 2번 질문의 제목과 내용")
    void t2() throws Exception {
        mvc.perform(get("/question/detail/2"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("스프링부트 모델 질문입니다.")))
                .andExpect(content().string(containsString("id는 자동으로 생성되나요?")))
                .andExpect(content().string(containsString("1개의 답변이 있습니다.")))
                .andExpect(content().string(containsString("네 자동으로 생성됩니다.")));
    }

    @Test
    @DisplayName("GET /question/detail/999 -> 없는 질문은 404")
    void t3() throws Exception {
        mvc.perform(get("/question/detail/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /question/create -> 질문 등록 폼")
    void t4() throws Exception {
        mvc.perform(get("/question/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("question_form"));
    }

    @Test
    @DisplayName("POST /question/create -> 저장 후 목록으로 리다이렉트")
    void t5() throws Exception {
        mvc.perform(post("/question/create")
                        .param("subject", "새 질문 제목")
                        .param("content", "새 질문 내용"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/question/list"));

        mvc.perform(get("/question/list"))
                .andExpect(content().string(containsString("새 질문 제목")));
    }

    @Test
    @DisplayName("POST /question/create, 빈 값 -> 저장하지 않고 폼과 에러를 다시 보여준다")
    void t6() throws Exception {
        mvc.perform(post("/question/create")
                        .param("subject", "")
                        .param("content", "내용은 입력함"))
                .andExpect(status().isOk())
                .andExpect(view().name("question_form"))
                .andExpect(model().attributeHasFieldErrors("form", "subject"))
                .andExpect(content().string(containsString("제목은 필수항목입니다.")))
                .andExpect(content().string(containsString("내용은 입력함"))); // 입력값 유지
    }
}
