package com.mysite.sbb.domain.question.answer.controller;

import com.mysite.sbb.domain.question.question.repository.QuestionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AnswerControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private QuestionRepository questionRepository;

    @Test
    @DisplayName("POST /answer/create/2 -> 답변 저장 후 상세 페이지로 리다이렉트")
    void t1() throws Exception {
        mvc.perform(post("/answer/create/2").param("content", "새 답변입니다."))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/question/detail/2"));

        assertThat(questionRepository.findById(2).get().getAnswers())
                .extracting("content")
                .contains("새 답변입니다.");
    }
}
