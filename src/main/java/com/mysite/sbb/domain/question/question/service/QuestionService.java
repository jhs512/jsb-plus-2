package com.mysite.sbb.domain.question.question.service;

import com.mysite.sbb.domain.question.question.entity.Question;
import com.mysite.sbb.domain.question.question.repository.QuestionRepository;
import com.mysite.sbb.global.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

// 컨트롤러는 요청/응답만, 서비스는 업무 로직을, 리포지터리는 DB 접근만 맡는다. (교재 2-09)
@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    // page: 0부터 시작하는 페이지 번호. 한 페이지에 10개, 최신 글이 먼저. (교재 3-02)
    public Page<Question> getList(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Order.desc("createDate"), Sort.Order.desc("id")));
        return questionRepository.findAll(pageable); // SELECT ... LIMIT 10 OFFSET page*10 + SELECT COUNT(*)
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
