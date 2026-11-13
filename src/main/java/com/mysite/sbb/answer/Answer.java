package com.mysite.sbb.answer;

import com.mysite.sbb.BaseEntity;
import com.mysite.sbb.question.Question;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
public class Answer extends BaseEntity {
    @Column(columnDefinition = "TEXT")
    private String content;

    // 답변(N) : 질문(1). answer 테이블에 question_id 외래키 컬럼이 생긴다.
    // @ManyToOne의 기본값은 EAGER(즉시 로딩)이므로 LAZY(필요할 때 로딩)를 명시한다. (14163 25강)
    @ManyToOne(fetch = LAZY)
    private Question question;
}
