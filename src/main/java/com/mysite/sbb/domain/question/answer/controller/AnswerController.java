package com.mysite.sbb.domain.question.answer.controller;

import com.mysite.sbb.domain.question.answer.service.AnswerService;
import com.mysite.sbb.domain.question.question.entity.Question;
import com.mysite.sbb.domain.question.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/answer")
@RequiredArgsConstructor
public class AnswerController {
    private final QuestionService questionService;
    private final AnswerService answerService;

    @PostMapping("/create/{id}")
    @Transactional // 조회 + 답변 추가를 하나의 트랜잭션으로 -> 메서드가 끝날 때 INSERT (14168 방식)
    public String createAnswer(
            @PathVariable int id,
            @RequestParam("content") String content // 폼의 name="content" 값
    ) {
        Question question = questionService.getQuestion(id);
        answerService.create(question, content);

        // 처리 후 화면을 직접 그리지 않고 상세 페이지로 다시 요청하게 한다. (POST-Redirect-GET)
        // -> 새로고침해도 답변이 중복 등록되지 않는다. (14168 28강)
        return "redirect:/question/detail/%d".formatted(id);
    }
}
