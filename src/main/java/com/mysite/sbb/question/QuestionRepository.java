package com.mysite.sbb.question;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// JpaRepository<엔티티, PK 타입>: save, findAll, findById, count, delete 등이 자동으로 구현된다.
// 메서드 이름 규칙(findBy + 필드명 + 조건)만 지키면 SQL이 자동으로 만들어진다. (14163 16, 17강)
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    // WHERE subject = ?
    Optional<Question> findBySubject(String subject);

    // WHERE subject = ? AND content = ?
    Optional<Question> findBySubjectAndContent(String subject, String content);

    // WHERE subject LIKE ?  (%는 호출하는 쪽에서 넣는다)
    List<Question> findBySubjectLike(String subject);

    // WHERE subject IN (?, ?)
    List<Question> findBySubjectIn(List<String> subjects);

    // WHERE id < ? ORDER BY id DESC
    List<Question> findByIdLessThanOrderByIdDesc(int id);
}
