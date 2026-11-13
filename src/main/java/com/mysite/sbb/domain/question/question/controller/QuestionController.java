package com.mysite.sbb.domain.question.question.controller;

import com.mysite.sbb.domain.question.question.entity.Question;
import com.mysite.sbb.domain.question.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor // final 필드를 받는 생성자를 만들어 준다 -> 생성자 주입
public class QuestionController {
    private final QuestionRepository questionRepository;

    // 타임리프 없이 자바 코드로 HTML을 직접 만들어 본다. (14163 27강)
    @GetMapping("/question/list")
    @ResponseBody
    public String list() {
        List<Question> questions = questionRepository.findAll();

        String lis = questions.stream()
                .map(q -> "<li>" + q.getId() + " : " + q.getSubject() + "</li>")
                .collect(Collectors.joining("\n"));

        return "<h1>질문 목록</h1>\n<ul>\n" + lis + "\n</ul>";
    }
}
