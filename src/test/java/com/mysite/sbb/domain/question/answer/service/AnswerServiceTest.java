package com.mysite.sbb.domain.question.answer.service;

import com.mysite.sbb.domain.question.answer.entity.Answer;
import com.mysite.sbb.domain.question.question.entity.Question;
import com.mysite.sbb.domain.question.question.repository.QuestionRepository;
import com.mysite.sbb.domain.user.user.entity.SiteUser;
import com.mysite.sbb.domain.user.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class AnswerServiceTest {
    @Autowired
    private AnswerService answerService;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private UserService userService;

    @Test
    @DisplayName("답변 페이징: 5개씩, 최신순")
    void t1() {
        Question q1 = questionRepository.findById(1).get();
        SiteUser user1 = userService.getUser("user1");
        SiteUser user2 = userService.getUser("user2");

        for (int i = 1; i <= 7; i++) q1.addAnswer(user1, "답변 %d".formatted(i));
        answerService.flush();


        Page<Answer> latest = answerService.getList(q1, 0);
        assertThat(latest.getTotalElements()).isEqualTo(7);
        assertThat(latest.getTotalPages()).isEqualTo(2);
        assertThat(latest.getContent()).hasSize(5);
        assertThat(latest.getContent().get(0).getContent()).isEqualTo("답변 7");

    }
}
