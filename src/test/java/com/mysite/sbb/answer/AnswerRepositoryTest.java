package com.mysite.sbb.answer;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional // 클래스에 붙이면 모든 테스트 메서드에 적용
class AnswerRepositoryTest {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;

    @Test
    @DisplayName("방법 1(교재): answerRepository.save로 답변 생성")
    void t1() {
        Question q2 = questionRepository.findById(2).get();

        Answer answer = new Answer();
        answer.setContent("답변 by answerRepository");
        answer.setQuestion(q2);
        answerRepository.save(answer);

        assertThat(answer.getId()).isGreaterThan(0); // save 하자마자 INSERT -> id 생성
        assertThat(answerRepository.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("방법 2(강사): question.addAnswer로 답변 생성, save 호출 없음")
    void t2() {
        Question q2 = questionRepository.findById(2).get();

        Answer answer = q2.addAnswer("답변 by addAnswer");

        // 아직 INSERT 전이라 id가 없다. 트랜잭션이 끝나거나 flush 할 때 INSERT 된다.
        assertThat(answer.getId()).isEqualTo(0);

        questionRepository.flush(); // 변경 감지 -> cascade PERSIST -> INSERT INTO answer

        assertThat(answer.getId()).isGreaterThan(0);
        assertThat(answerRepository.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("답변에서 질문 조회(ManyToOne), 질문에서 답변 목록 조회(OneToMany)")
    void t3() {
        Answer answer = answerRepository.findById(1).get();
        assertThat(answer.getQuestion().getId()).isEqualTo(2);

        Question q2 = questionRepository.findById(2).get();
        assertThat(q2.getAnswers()).extracting(Answer::getContent)
                .containsExactly("네 자동으로 생성됩니다.");
    }
}
