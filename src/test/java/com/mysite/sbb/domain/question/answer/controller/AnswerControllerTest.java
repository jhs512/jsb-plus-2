package com.mysite.sbb.domain.question.answer.controller;

import com.mysite.sbb.domain.question.answer.repository.AnswerRepository;
import com.mysite.sbb.domain.question.question.repository.QuestionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
@WithUserDetails("user2")
class AnswerControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;

    @Test
    @DisplayName("POST /answer/create/2 -> 답변 저장 후 상세 페이지로 리다이렉트")
    void t1() throws Exception {
        mvc.perform(post("/answer/create/2").with(csrf()).param("content", "새 답변입니다."))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/question/detail/2"));

        assertThat(questionRepository.findById(2).get().getAnswers())
                .extracting("content")
                .contains("새 답변입니다.");
    }

    @Test
    @DisplayName("POST /answer/create/2, 빈 내용 -> 저장하지 않고 상세 화면과 에러를 보여준다")
    void t2() throws Exception {
        mvc.perform(post("/answer/create/2").with(csrf()).param("content", "  "))
                .andExpect(status().isOk())
                .andExpect(view().name("question_detail"))
                .andExpect(model().attributeHasFieldErrors("answerForm", "content"));

        assertThat(questionRepository.findById(2).get().getAnswers()).hasSize(1);
    }

    @Test
    @DisplayName("PUT /answer/modify/1 (본인) -> 수정 후 질문 상세로")
    void t3() throws Exception {
        mvc.perform(put("/answer/modify/1").with(csrf()).param("content", "수정된 답변"))
                .andExpect(redirectedUrl("/question/detail/2"));

        assertThat(answerRepository.findById(1).get().getContent()).isEqualTo("수정된 답변");
    }

    @Test
    @DisplayName("남의 답변 수정 -> 403")
    @org.springframework.security.test.context.support.WithUserDetails("user1")
    void t4() throws Exception {
        mvc.perform(put("/answer/modify/1").with(csrf()).param("content", "남의 답변"))
                .andExpect(status().isForbidden());
    }

}
