package com.mysite.sbb.domain.question.question.repository;

import com.mysite.sbb.domain.question.category.entity.Category;
import com.mysite.sbb.domain.question.question.entity.Question;
import com.mysite.sbb.domain.user.user.entity.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    // 조회 수 +1을 DB에서 한 번에 처리한다. (UPDATE ... SET view_count = view_count + 1)
    // 엔티티를 읽고 +1 후 저장하면, 동시에 두 명이 조회할 때 하나가 덮어써져 1만 오를 수 있다(갱신 분실).
    // clearAutomatically: UPDATE 후 영속성 컨텍스트를 비워, 다음 조회 때 바뀐 값을 다시 읽게 한다.
    @Modifying(clearAutomatically = true)
    @Query("update Question q set q.viewCount = q.viewCount + 1 where q.id = :id")
    int increaseViewCount(@Param("id") int id);

    // 프로필: 내가 쓴 질문 최근 10개, 개수
    List<Question> findTop10ByAuthorOrderByCreateDateDesc(SiteUser author);

    long countByAuthor(SiteUser author);

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
            where (:category is null or q.category = :category)
              and (
                    q.subject like %:kw%
                 or q.content like %:kw%
                 or u1.username like %:kw%
                 or a.content like %:kw%
                 or u2.username like %:kw%
              )
            """)
    Page<Question> findAllByKeyword(@Param("kw") String kw, @Param("category") Category category, Pageable pageable);
}
