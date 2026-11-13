package com.mysite.sbb.domain.question.comment.service;

import com.mysite.sbb.domain.question.comment.entity.Comment;
import com.mysite.sbb.domain.question.comment.repository.CommentRepository;
import com.mysite.sbb.global.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public List<Comment> getRecent() {
        return commentRepository.findTop10ByOrderByCreateDateDescIdDesc();
    }

    public Comment getComment(int id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("comment not found"));
    }

    // 부모(질문 또는 답변)의 댓글 목록에서 제거 -> orphanRemoval로 DELETE (14168 37강)
    public void delete(Comment comment) {
        if (comment.getQuestion() != null) comment.getQuestion().deleteComment(comment);
        else comment.getAnswer().deleteComment(comment);
    }
}
