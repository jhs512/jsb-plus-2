package com.mysite.sbb.domain.question.question.repository;

import com.mysite.sbb.domain.question.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // 검색 (교재 3-13): 질문 제목, 질문 내용, 질문 글쓴이, 답변 내용, 답변 글쓴이 중 하나라도 kw를 포함하면 조회
    // 메서드 이름 규칙으로는 너무 길어지므로 JPQL(엔티티 기준 SQL)을 직접 쓴다.
    // LEFT JOIN: 답변이 없는 질문도 포함, DISTINCT: 답변이 여러 개 일치해도 질문은 한 번만
    @Query("""
            select distinct q
            from Question q
            left join q.author u1
            left join q.answers a
            left join a.author u2
            where q.subject like %:kw%
               or q.content like %:kw%
               or u1.username like %:kw%
               or a.content like %:kw%
               or u2.username like %:kw%
            """)
    Page<Question> findAllByKeyword(@Param("kw") String kw, Pageable pageable);
}
