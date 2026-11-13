package com.mysite.sbb.domain.question.answer.repository;

import com.mysite.sbb.domain.question.answer.entity.Answer;
import com.mysite.sbb.domain.question.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    // 정렬은 Pageable의 Sort로 (최신순: createDate desc)
    Page<Answer> findByQuestion(Question question, Pageable pageable);
}
