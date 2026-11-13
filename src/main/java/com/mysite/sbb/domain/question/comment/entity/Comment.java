package com.mysite.sbb.domain.question.comment.entity;

import com.mysite.sbb.domain.question.answer.entity.Answer;
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

// 댓글 (도전과제 3-14). 질문에 달리거나(question), 답변에 달린다(answer). 둘 중 하나만 채워진다.
@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Comment extends BaseEntity {
    @ManyToOne(fetch = LAZY)
    private SiteUser author;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = LAZY)
    private Question question;

    @ManyToOne(fetch = LAZY)
    private Answer answer;

    public Comment(SiteUser author, String content, Question question, Answer answer) {
        this.author = author;
        this.content = content;
        this.question = question;
        this.answer = answer;
    }

    public boolean isAuthor(SiteUser user) {
        return user != null && author != null && author.getId() == user.getId();
    }

    // 댓글이 속한 질문 id (답변 댓글이면 답변의 질문)
    public int getRootQuestionId() {
        return question != null ? question.getId() : answer.getQuestion().getId();
    }
}
