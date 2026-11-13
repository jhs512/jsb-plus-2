package com.mysite.sbb.domain.question.answer.entity;

import com.mysite.sbb.domain.question.question.entity.Question;
import com.mysite.sbb.domain.user.user.entity.SiteUser;
import com.mysite.sbb.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Answer extends BaseEntity {
    // 답변(N) : 질문(1). answer 테이블에 question_id 외래키 컬럼이 생긴다.
    // @ManyToOne의 기본값은 EAGER(즉시 로딩)이므로 LAZY(필요할 때 로딩)를 명시한다. (14163 25강)
    @ManyToOne(fetch = LAZY)
    private Question question;

    @ManyToOne(fetch = LAZY)
    private SiteUser author;

    @Column(columnDefinition = "TEXT")
    private String content;

    public Answer(Question question, SiteUser author, String content) {
        this.question = question;
        this.author = author;
        this.content = content;
    }

    public void modify(String content) {
        this.content = content;
    }
}
