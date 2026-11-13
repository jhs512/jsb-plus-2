package com.mysite.sbb.question;

import com.mysite.sbb.BaseEntity;
import com.mysite.sbb.answer.Answer;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity // 이 클래스를 기준으로 question 테이블이 만들어진다.
@Getter
@Setter
public class Question extends BaseEntity {
    @Column(length = 200) // varchar(200)
    private String subject;

    @Column(columnDefinition = "TEXT") // 길이 제한 없는 텍스트
    private String content;

    // 질문(1) : 답변(N). DB 컬럼은 생기지 않는다.
    // mappedBy = "question": 관계의 주인은 Answer.question(question_id 외래키)이다. -> 중간 테이블 없음 (14163 12강)
    // PERSIST: 이 리스트에 새로 추가된 답변은 자동 INSERT, REMOVE: 질문 삭제 시 답변도 자동 DELETE (14163 21강)
    // new ArrayList<>(): null이 아니므로 바로 add 가능 (14163 23강)
    @OneToMany(mappedBy = "question", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Answer> answers = new ArrayList<>();

    // 답변 생성을 Question에게 맡긴다. answerRepository.save 없이도
    // cascade PERSIST 덕분에 트랜잭션이 끝날 때 자동 INSERT 된다. (14163 21강)
    public Answer addAnswer(String content) {
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setQuestion(this); // 관계의 주인 쪽(question_id)을 채워야 DB에 반영된다.
        answers.add(answer);      // 자바 객체 쪽 관계도 함께 맞춰 준다.

        return answer;
    }
}
