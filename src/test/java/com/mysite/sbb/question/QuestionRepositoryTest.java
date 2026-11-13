package com.mysite.sbb.question;

import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    @DisplayName("findBySubject")
    void t3() {
        Question q = questionRepository.findBySubject("sbb가 무엇인가요?").get();
        assertThat(q.getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("findBySubjectAndContent")
    void t4() {
        Question q = questionRepository.findBySubjectAndContent(
                "sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다.").get();
        assertThat(q.getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("findBySubjectLike")
    void t5() {
        List<Question> questions = questionRepository.findBySubjectLike("sbb%");
        assertThat(questions).hasSize(1);
    }

    @Test
    @DisplayName("findBySubjectIn")
    void t6() {
        List<Question> questions = questionRepository.findBySubjectIn(
                List.of("sbb가 무엇인가요?", "스프링부트 모델 질문입니다."));
        assertThat(questions).hasSize(2);
    }

    @Test
    @DisplayName("findByIdLessThanOrderByIdDesc")
    void t7() {
        List<Question> questions = questionRepository.findByIdLessThanOrderByIdDesc(3);
        assertThat(questions).extracting(Question::getId).containsExactly(2, 1);
    }

    @Test
    @DisplayName("[실험] 트랜잭션 밖에서 LAZY 컬렉션에 접근하면 LazyInitializationException")
    void t8() {
        // findById가 끝나는 순간 DB 연결(영속성 컨텍스트)이 닫힌다.
        Question q2 = questionRepository.findById(2).get();

        // answers는 LAZY라 아직 SELECT 되지 않았고, 이제는 가져올 연결이 없다.
        assertThatThrownBy(() -> q2.getAnswers().size())
                .isInstanceOf(LazyInitializationException.class);
    }
}
