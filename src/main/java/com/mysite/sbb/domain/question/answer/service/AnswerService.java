package com.mysite.sbb.domain.question.answer.service;

import com.mysite.sbb.domain.question.answer.entity.Answer;
import com.mysite.sbb.domain.question.answer.repository.AnswerRepository;
import com.mysite.sbb.domain.question.question.entity.Question;
import com.mysite.sbb.domain.user.user.entity.SiteUser;
import com.mysite.sbb.global.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;

    // answerRepository.save 대신 question.addAnswer (14163 37강 강사의 커스터마이징)
    // 호출하는 쪽의 트랜잭션이 끝날 때 cascade PERSIST로 INSERT 된다.
    public Answer create(Question question, SiteUser author, String content) {
        return question.addAnswer(author, content);
    }

    // 답변 페이징과 정렬 (도전과제 3-14). sort: latest(최신순, 기본) | vote(추천순)
    public Page<Answer> getList(Question question, int page, String sort) {
        if ("vote".equals(sort)) {
            return answerRepository.findByQuestionOrderByVoteCount(question, PageRequest.of(page, 5));
        }

        Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Order.desc("createDate"), Sort.Order.desc("id")));
        return answerRepository.findByQuestion(question, pageable);
    }

    public void flush() {
        answerRepository.flush();
    }

    public Answer getAnswer(int id) {
        return answerRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("answer not found"));
    }

    public void modify(Answer answer, String content) {
        answer.modify(content);
    }

    // answerRepository.delete 대신 질문의 답변 목록에서 제거 -> orphanRemoval로 DELETE (14168 37강)
    public void delete(Answer answer) {
        answer.getQuestion().deleteAnswer(answer);
    }
}
