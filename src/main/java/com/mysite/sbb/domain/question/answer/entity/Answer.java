package com.mysite.sbb.domain.question.answer.entity;

import com.mysite.sbb.domain.question.question.entity.Question;
import com.mysite.sbb.domain.question.comment.entity.Comment;
import com.mysite.sbb.domain.user.user.entity.SiteUser;
import com.mysite.sbb.global.jpa.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    // 추천인. 질문(N) : 회원(N) -> 중간 테이블이 필요하다. (교재 3-10)
    // Set: 같은 사람이 여러 번 추천해도 한 번만 들어간다.
    @ManyToMany
    private Set<SiteUser> voters = new HashSet<>();

    // 댓글. 14168 Post-PostComment와 같은 방식: 추가는 PERSIST, 부모 삭제 시 REMOVE, 목록에서 빼면 orphanRemoval
    @OneToMany(mappedBy = "answer", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

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

    public boolean isAuthor(SiteUser user) {
        return user != null && author != null && author.getId() == user.getId();
    }

    public void vote(SiteUser voter) {
        voters.add(voter); // 이미 있으면 무시 (equals/hashCode가 id 기준이라 가능)
    }

    public Comment addComment(SiteUser author, String content) {
        Comment comment = new Comment(author, content, null, this);
        comments.add(comment);
        return comment;
    }

    public boolean deleteComment(Comment comment) {
        return comments.remove(comment);
    }
}
