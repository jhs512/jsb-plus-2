package com.mysite.sbb.domain.question.question.repository;

import com.mysite.sbb.domain.question.question.entity.Question;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// test 프로파일(메모리 DB) + BaseInitData가 만든 샘플 데이터(질문 2개, 답변 1개)를 기준으로 테스트한다.
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

    @Test
    @DisplayName("@Transactional 안에서는 LAZY 컬렉션을 읽을 수 있다")
    @Transactional // 테스트 메서드 전체가 하나의 트랜잭션 -> 끝나면 롤백
    void t9() {
        Question q2 = questionRepository.findById(2).get();

        // 이 시점에 SELECT ... FROM answer WHERE question_id = 2 가 실행된다.
        assertThat(q2.getAnswers()).hasSize(1);
        assertThat(q2.getAnswers().get(0).getContent()).isEqualTo("네 자동으로 생성됩니다.");
    }

    @Test
    @DisplayName("데이터 수정: 테스트가 끝나면 롤백되어 다른 테스트에 영향이 없다")
    @Transactional
    void t10() {
        Question q = questionRepository.findById(1).get();
        q.modify("수정된 제목", q.getContent());
        questionRepository.flush(); // UPDATE 를 지금 바로 실행 (로그로 확인용)

        assertThat(questionRepository.findById(1).get().getSubject()).isEqualTo("수정된 제목");
        // 이 메서드가 끝나면 롤백 -> t2, t3는 여전히 "sbb가 무엇인가요?"를 본다. (14163 18강)
    }

    @Test
    @DisplayName("데이터 삭제")
    @Transactional
    void t11() {
        assertThat(questionRepository.count()).isEqualTo(2);

        Question q = questionRepository.findById(1).get();
        questionRepository.delete(q);

        assertThat(questionRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("질문을 삭제하면 cascade REMOVE로 답변도 함께 삭제된다")
    @Transactional
    void t12() {
        Question q2 = questionRepository.findById(2).get();
        questionRepository.delete(q2);
        questionRepository.flush(); // DELETE answer ... -> DELETE question ... 순서로 실행

        assertThat(questionRepository.findById(2)).isEmpty();
    }

    @Test
    @DisplayName("검색: 제목/내용/글쓴이/답변 내용/답변 글쓴이")
    void t13() {
        assertThat(search("sbb가")).containsExactly(1);          // 질문 제목
        assertThat(search("자동으로 생성되나요")).containsExactly(2); // 질문 내용
        assertThat(search("네 자동으로")).containsExactly(2);      // 답변 내용
        assertThat(search("user2")).containsExactly(2);           // 답변 글쓴이
        assertThat(search("user1")).containsExactlyInAnyOrder(1, 2); // 질문 글쓴이
        assertThat(search("")).hasSize(2);                         // 빈 검색어 = 전체
        assertThat(search("없는검색어")).isEmpty();
    }

    private List<Integer> search(String kw) {
        Page<Question> page = questionRepository.findAllByKeyword(kw, PageRequest.of(0, 10));
        return page.getContent().stream().map(Question::getId).toList();
    }
}
