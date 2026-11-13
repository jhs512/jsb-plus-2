package com.mysite.sbb.domain.question.question.controller;

import com.mysite.sbb.domain.question.question.entity.Question;
import com.mysite.sbb.domain.question.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/question") // 이 컨트롤러의 모든 URL 앞에 /question 이 붙는다. (교재 2-11)
@RequiredArgsConstructor // final 필드를 받는 생성자를 만들어 준다 -> 생성자 주입
public class QuestionController {
    private final QuestionService questionService;

    // @ResponseBody가 없으므로 반환값 "question_list"는 템플릿 이름이 된다.
    // -> templates/question_list.html 을 렌더링한 HTML이 응답 본문이 된다. (14163 28강)
    @GetMapping("/list")
    public String list(Model model) {
        List<Question> questionList = questionService.getList();
        model.addAttribute("questionList", questionList); // 템플릿에서 ${questionList}로 사용

        return "question_list";
    }

    // {id} 자리의 값이 @PathVariable int id 로 들어온다. 예) /question/detail/2 -> id = 2
    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable int id) {
        Question question = questionService.getQuestion(id);
        model.addAttribute("question", question);

        return "question_detail";
    }

    // GET: 폼을 보여준다. POST: 폼 데이터를 처리한다. URL이 같아도 메서드로 구분된다. (14168 26강)
    @GetMapping("/create")
    public String showCreate() {
        return "question_form";
    }

    @PostMapping("/create")
    public String create(
            @RequestParam("subject") String subject,
            @RequestParam("content") String content
    ) {
        questionService.create(subject, content);

        return "redirect:/question/list";
    }
}
