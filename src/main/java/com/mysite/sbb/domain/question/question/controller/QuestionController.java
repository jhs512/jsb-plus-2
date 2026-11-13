package com.mysite.sbb.domain.question.question.controller;

import com.mysite.sbb.domain.question.question.entity.Question;
import com.mysite.sbb.domain.question.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor // final 필드를 받는 생성자를 만들어 준다 -> 생성자 주입
public class QuestionController {
    private final QuestionService questionService;

    // @ResponseBody가 없으므로 반환값 "question_list"는 템플릿 이름이 된다.
    // -> templates/question_list.html 을 렌더링한 HTML이 응답 본문이 된다. (14163 28강)
    @GetMapping("/question/list")
    public String list(Model model) {
        List<Question> questionList = questionService.getList();
        model.addAttribute("questionList", questionList); // 템플릿에서 ${questionList}로 사용

        return "question_list";
    }

    // {id} 자리의 값이 @PathVariable int id 로 들어온다. 예) /question/detail/2 -> id = 2
    @GetMapping("/question/detail/{id}")
    public String detail(Model model, @PathVariable int id) {
        Question question = questionService.getQuestion(id);
        model.addAttribute("question", question);

        return "question_detail";
    }
}
