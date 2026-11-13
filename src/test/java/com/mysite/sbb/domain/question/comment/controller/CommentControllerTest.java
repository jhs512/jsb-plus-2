package com.mysite.sbb.domain.question.comment.controller;

import com.mysite.sbb.domain.question.comment.repository.CommentRepository;
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
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithUserDetails("user1")
class CommentControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private QuestionRepository questionRepository;

    @Test
    @DisplayName("질문에 댓글 작성 -> 상세 화면에 표시")
    void t1() throws Exception {
        mvc.perform(post("/comment/create/question/1").with(csrf()).param("content", "질문 댓글입니다."))
                .andExpect(redirectedUrl("/question/detail/1"));

        mvc.perform(get("/question/detail/1"))
                .andExpect(content().string(containsString("질문 댓글입니다.")));
    }

    @Test
    @DisplayName("답변에 댓글 작성 -> 답변 위치로 이동")
    void t2() throws Exception {
        mvc.perform(post("/comment/create/answer/1").with(csrf()).param("content", "답변 댓글"))
                .andExpect(redirectedUrl("/question/detail/2#answer_1"));

        commentRepository.flush();
        assertThat(commentRepository.findAll()).extracting("content").contains("답변 댓글");
    }

    @Test
    @DisplayName("빈 댓글(JS 검사를 우회한 요청) -> 서버 검증으로 400")
    void t3() throws Exception {
        mvc.perform(post("/comment/create/question/1").with(csrf()).param("content", " "))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("본인 댓글 삭제 -> orphanRemoval로 DELETE, 남의 댓글 삭제 -> 403")
    void t4() throws Exception {
        mvc.perform(post("/comment/create/question/1").with(csrf()).param("content", "지울 댓글"));
        commentRepository.flush();
        int id = questionRepository.findById(1).get().getComments().get(0).getId();

        mvc.perform(delete("/comment/delete/" + id).with(csrf()).with(user("user2")))
                .andExpect(status().isForbidden());

        mvc.perform(delete("/comment/delete/" + id).with(csrf()))
                .andExpect(redirectedUrl("/question/detail/1"));
        commentRepository.flush();
        assertThat(commentRepository.findById(id)).isEmpty();
    }
}
