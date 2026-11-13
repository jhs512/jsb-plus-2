package com.mysite.sbb.domain.question.question.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import com.mysite.sbb.domain.user.user.service.UserService;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import com.mysite.sbb.domain.question.question.service.QuestionService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // 테스트에서 등록한 질문은 롤백
@WithUserDetails("user1") // 모든 테스트를 user1로 로그인한 상태에서 실행
class QuestionControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private UserService userService;

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
        mvc.perform(post("/question/create").with(csrf())
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
        mvc.perform(post("/question/create").with(csrf())
                        .param("subject", "")
                        .param("content", "내용은 입력함"))
                .andExpect(status().isOk())
                .andExpect(view().name("question_form"))
                .andExpect(model().attributeHasFieldErrors("form", "subject"))
                .andExpect(content().string(containsString("data-error-field-name=\"subject\"")))
                .andExpect(content().string(containsString(">제목은 필수항목입니다.</li>"))) // 접두어 01-subject- 제거됨
                .andExpect(content().string(containsString("내용은 입력함"))); // 입력값 유지
    }

    @Test
    @DisplayName("페이징: 한 페이지에 10개, 최신 글이 먼저")
    void t7() throws Exception {
        for (int i = 1; i <= 25; i++) questionService.create(userService.getUser("user1"), "페이징 테스트 %02d".formatted(i), "내용");
        // 총 27개 -> 3페이지 (10, 10, 7)

        mvc.perform(get("/question/list"))
                .andExpect(content().string(containsString("페이징 테스트 25"))) // 가장 최신 글
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("페이징 테스트 15"))));

        mvc.perform(get("/question/list").param("page", "2"))
                .andExpect(content().string(containsString("sbb가 무엇인가요?"))); // 가장 오래된 글은 마지막 페이지
    }

    @Test
    @DisplayName("로그인하지 않으면 질문 등록 폼 대신 로그인 페이지로")
    @WithAnonymousUser
    void t8() throws Exception {
        mvc.perform(get("/question/create"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/user/login"));
    }

    @Test
    @DisplayName("등록한 질문의 글쓴이는 로그인한 사용자")
    void t9() throws Exception {
        mvc.perform(post("/question/create").with(csrf())
                .param("subject", "글쓴이 테스트")
                .param("content", "내용"));

        org.assertj.core.api.Assertions.assertThat(
                questionService.getList(0).getContent().get(0).getAuthor().getUsername()
        ).isEqualTo("user1");
    }

    @Test
    @DisplayName("수정 폼: 기존 값이 채워지고 _method=PUT 포함")
    void t10() throws Exception {
        mvc.perform(get("/question/modify/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("sbb가 무엇인가요?")))
                .andExpect(content().string(containsString("name=\"_method\" value=\"PUT\"")));
    }

    @Test
    @DisplayName("PUT /question/modify/1 (본인) -> 수정 후 상세로")
    void t11() throws Exception {
        mvc.perform(put("/question/modify/1").with(csrf())
                        .param("subject", "수정된 제목")
                        .param("content", "수정된 내용"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/question/detail/1"));

        org.assertj.core.api.Assertions.assertThat(questionService.getQuestion(1).getSubject()).isEqualTo("수정된 제목");
    }

    @Test
    @DisplayName("POST + _method=PUT 도 PUT으로 처리된다 (HiddenHttpMethodFilter)")
    void t12() throws Exception {
        mvc.perform(post("/question/modify/1").with(csrf())
                        .param("_method", "PUT")
                        .param("subject", "히든 메서드로 수정")
                        .param("content", "내용"))
                .andExpect(redirectedUrl("/question/detail/1"));
    }

    @Test
    @DisplayName("남의 글 수정 -> 403")
    @WithUserDetails("user2")
    void t13() throws Exception {
        mvc.perform(put("/question/modify/1").with(csrf())
                        .param("subject", "남의 글")
                        .param("content", "내용"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /question/delete/2 (본인) -> 삭제 후 / 로, 답변도 함께 삭제")
    void t14() throws Exception {
        mvc.perform(delete("/question/delete/2").with(csrf()))
                .andExpect(redirectedUrl("/"));

        mvc.perform(get("/question/detail/2")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET 으로는 삭제할 수 없다 -> 405")
    void t15() throws Exception {
        mvc.perform(get("/question/delete/2"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("POST /question/vote/1 -> 추천, 두 번 눌러도 1표")
    void t16() throws Exception {
        mvc.perform(post("/question/vote/1").with(csrf())).andExpect(redirectedUrl("/question/detail/1"));
        mvc.perform(post("/question/vote/1").with(csrf()));

        org.assertj.core.api.Assertions.assertThat(questionService.getQuestion(1).getVoters()).hasSize(1);
    }

    @Test
    @DisplayName("GET /question/list?kw=user2 -> 검색 결과와 검색어 유지")
    void t17() throws Exception {
        mvc.perform(get("/question/list").param("kw", "user2"))
                .andExpect(content().string(containsString("스프링부트 모델 질문입니다.")))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("sbb가 무엇인가요?"))))
                .andExpect(content().string(containsString("value=\"user2\"")))
                .andExpect(content().string(containsString("/question/list?page=0&amp;kw=user2")));
    }
}
