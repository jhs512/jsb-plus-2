package com.mysite.sbb.domain.question.comment.repository;

import com.mysite.sbb.domain.question.comment.entity.Comment;
import com.mysite.sbb.domain.user.user.entity.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findTop10ByAuthorOrderByCreateDateDesc(SiteUser author);

    long countByAuthor(SiteUser author);

    // 최근 댓글 (전체)
    List<Comment> findTop10ByOrderByCreateDateDescIdDesc();
}
