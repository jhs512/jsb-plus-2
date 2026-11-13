package com.mysite.sbb.question;

import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository<엔티티, PK 타입>: save, findAll, findById, count, delete 등이 자동으로 구현된다.
public interface QuestionRepository extends JpaRepository<Question, Integer> {
}
