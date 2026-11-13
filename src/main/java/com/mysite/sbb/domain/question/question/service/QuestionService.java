package com.mysite.sbb.domain.question.question.service;

import com.mysite.sbb.domain.question.question.entity.Question;
import com.mysite.sbb.domain.question.question.repository.QuestionRepository;
import com.mysite.sbb.global.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// 컨트롤러는 요청/응답만, 서비스는 업무 로직을, 리포지터리는 DB 접근만 맡는다. (교재 2-09)
@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    public List<Question> getList() {
        return questionRepository.findAll();
    }

    public Question getQuestion(int id) {
        // 없으면 500(No value present) 대신 404(question not found)
        return questionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("question not found"));
    }

    public Question create(String subject, String content) {
        return questionRepository.save(new Question(subject, content));
    }
}
