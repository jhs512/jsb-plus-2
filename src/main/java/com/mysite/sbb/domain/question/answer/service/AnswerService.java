package com.mysite.sbb.domain.question.answer.service;

import com.mysite.sbb.domain.question.answer.entity.Answer;
import com.mysite.sbb.domain.question.answer.repository.AnswerRepository;
import com.mysite.sbb.domain.question.question.entity.Question;
import com.mysite.sbb.domain.user.user.entity.SiteUser;
import com.mysite.sbb.global.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
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

    public Answer getAnswer(int id) {
        return answerRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("answer not found"));
    }

    public void modify(Answer answer, String content) {
        answer.modify(content);
    }
}
