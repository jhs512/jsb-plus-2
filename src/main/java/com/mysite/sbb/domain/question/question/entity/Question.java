package com.mysite.sbb.domain.question.question.entity;

import com.mysite.sbb.domain.question.answer.entity.Answer;
import com.mysite.sbb.domain.question.category.entity.Category;
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

@Entity // 이 클래스를 기준으로 question 테이블이 만들어진다.
@Getter
@NoArgsConstructor(access = PROTECTED) // JPA가 객체를 만들 때 필요한 기본 생성자 (외부에서는 사용 금지)
public class Question extends BaseEntity {
    // 글쓴이. 질문(N) : 회원(1) (교재 3-08)
    @ManyToOne(fetch = LAZY)
    private SiteUser author;

    // 카테고리 (도전과제 3-14). 질문(N) : 카테고리(1)
    @ManyToOne(fetch = LAZY)
    private Category category;

    // 추천인. 질문(N) : 회원(N) -> 중간 테이블이 필요하다. (교재 3-10)
    // Set: 같은 사람이 여러 번 추천해도 한 번만 들어간다.
    @ManyToMany
    private Set<SiteUser> voters = new HashSet<>();

    // 댓글. 14168 Post-PostComment와 같은 방식: 추가는 PERSIST, 부모 삭제 시 REMOVE, 목록에서 빼면 orphanRemoval
    @OneToMany(mappedBy = "question", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Column(length = 200) // varchar(200)
    private String subject;

    @Column(columnDefinition = "TEXT") // 길이 제한 없는 텍스트
    private String content;

    // 조회 수 (도전과제 3-14). 기존 행에도 0이 들어가도록 DB 기본값 지정
    @Column(columnDefinition = "integer default 0 not null")
    private int viewCount;

    // 질문(1) : 답변(N). DB 컬럼은 생기지 않는다.
    // mappedBy = "question": 관계의 주인은 Answer.question(question_id 외래키)이다. -> 중간 테이블 없음 (14163 12강)
    // PERSIST: 이 리스트에 새로 추가된 답변은 자동 INSERT, REMOVE: 질문 삭제 시 답변도 자동 DELETE (14163 21강)
    // new ArrayList<>(): null이 아니므로 바로 add 가능 (14163 23강)
    // orphanRemoval: answers 리스트에서 빠진 답변(고아)은 자동 DELETE (14168 37강)
    @OneToMany(mappedBy = "question", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();

    // @Setter 대신 "만들 때 필요한 값"을 생성자로 받는다. (14168)
    public Question(SiteUser author, Category category, String subject, String content) {
        this.author = author;
        this.category = category;
        this.subject = subject;
        this.content = content;
    }

    // 값을 바꾸는 경로를 의미 있는 메서드 하나로 제한한다. (14168 32강)
    public void modify(Category category, String subject, String content) {
        this.category = category;
        this.subject = subject;
        this.content = content;
    }

    // 글쓴이 확인. author.getId()는 LAZY 프록시를 초기화하지 않고도 읽을 수 있다.
    public boolean isAuthor(SiteUser user) {
        return user != null && author != null && author.getId() == user.getId();
    }

    // 답변 생성을 Question에게 맡긴다. answerRepository.save 없이도
    // cascade PERSIST 덕분에 트랜잭션이 끝날 때 자동 INSERT 된다. (14163 21강)
    public Answer addAnswer(SiteUser author, String content) {
        Answer answer = new Answer(this, author, content); // 관계의 주인 쪽(question_id)을 채워야 DB에 반영된다.
        answers.add(answer);                        // 자바 객체 쪽 관계도 함께 맞춰 준다.

        return answer;
    }

    public boolean deleteAnswer(Answer answer) {
        return answers.remove(answer); // 리스트에서 제거 -> orphanRemoval로 DELETE
    }

    public void vote(SiteUser voter) {
        voters.add(voter); // 이미 있으면 무시 (equals/hashCode가 id 기준이라 가능)
    }

    public Comment addComment(SiteUser author, String content) {
        Comment comment = new Comment(author, content, this, null);
        comments.add(comment);
        return comment;
    }

    public boolean deleteComment(Comment comment) {
        return comments.remove(comment);
    }
}
