package com.mysite.sbb.domain.question.answer.repository;

import com.mysite.sbb.domain.question.answer.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
}
