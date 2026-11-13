package com.mysite.sbb.domain.question.comment.repository;

import com.mysite.sbb.domain.question.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
