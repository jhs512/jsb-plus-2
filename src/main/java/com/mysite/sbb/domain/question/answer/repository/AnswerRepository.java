package com.mysite.sbb.domain.question.answer.repository;

import com.mysite.sbb.domain.question.answer.entity.Answer;
import com.mysite.sbb.domain.question.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    // 정렬은 Pageable의 Sort로 (최신순: createDate desc)
    Page<Answer> findByQuestion(Question question, Pageable pageable);

    // 추천순: 추천인 수(size(voters))로 정렬해야 해서 JPQL 사용. 추천 수가 같으면 최신순
    @Query(value = """
            select a
            from Answer a
            where a.question = :question
            order by size(a.voters) desc, a.createDate desc, a.id desc
            """,
            countQuery = "select count(a) from Answer a where a.question = :question")
    Page<Answer> findByQuestionOrderByVoteCount(@Param("question") Question question, Pageable pageable);
}
