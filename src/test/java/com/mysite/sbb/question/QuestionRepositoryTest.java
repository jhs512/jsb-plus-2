package com.mysite.sbb.question;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// test 프로파일(메모리 DB) + TestInitData가 만든 샘플 데이터(질문 2개, 답변 1개)를 기준으로 테스트한다.
@ActiveProfiles("test")
@SpringBootTest
class QuestionRepositoryTest {
    @Autowired
    private QuestionRepository questionRepository;

    @Test
    @DisplayName("findAll")
    void t1() {
        List<Question> questions = questionRepository.findAll();
        assertThat(questions).hasSize(2);

        Question q1 = questions.get(0);
        assertThat(q1.getSubject()).isEqualTo("sbb가 무엇인가요?");
        assertThat(q1.getCreateDate()).isNotNull(); // Auditing이 자동으로 채웠다.
    }

    @Test
    @DisplayName("findById")
    void t2() {
        Question q = questionRepository.findById(1).get();
        assertThat(q.getSubject()).isEqualTo("sbb가 무엇인가요?");
    }
}
