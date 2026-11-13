package com.mysite.sbb.domain.question.question.controller;

import com.mysite.sbb.domain.question.answer.controller.AnswerController.AnswerForm;
import com.mysite.sbb.domain.question.question.entity.Question;
import com.mysite.sbb.domain.question.question.service.QuestionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String detail(
            Model model,
            @PathVariable int id,
            @ModelAttribute("answerForm") AnswerForm answerForm // 답변 폼(th:object)에 쓸 빈 객체
    ) {
        Question question = questionService.getQuestion(id);
        model.addAttribute("question", question);

        return "question_detail";
    }

    // GET: 폼을 보여준다. POST: 폼 데이터를 처리한다. URL이 같아도 메서드로 구분된다. (14168 26강)
    // 폼 데이터를 담는 객체. record: 생성자, getter, equals 등이 자동으로 생기는 불변 데이터 클래스 (14168 40강)
    // 메시지 형식 "순번-필드명-내용": 정렬해서 보여주고, 첫 번째 에러 필드에 focus 하기 위함 (14168 14, 15강)
    public record QuestionForm(
            @NotBlank(message = "01-subject-제목은 필수항목입니다.")
            @Size(max = 200, message = "02-subject-제목은 200자 이하로 입력해주세요.")
            String subject,

            @NotBlank(message = "03-content-내용은 필수항목입니다.")
            String content
    ) {
    }

    // @ModelAttribute("form"): 빈 폼 객체가 모델에 "form"이라는 이름으로 담긴다. -> 템플릿에서 th:object="${form}"
    @GetMapping("/create")
    public String showCreate(@ModelAttribute("form") QuestionForm form) {
        return "question_form";
    }

    @PostMapping("/create")
    public String create(
            @ModelAttribute("form") @Valid QuestionForm form, // 요청 파라미터 -> QuestionForm, 그리고 검증
            BindingResult bindingResult // 검증 결과. 꼭 @Valid 바로 뒤에 와야 한다. (14168 13강)
    ) {
        // 검증 실패: 예외 대신 폼을 다시 보여준다. 입력값과 에러는 form, bindingResult에 담겨 있다.
        if (bindingResult.hasErrors()) return "question_form";

        questionService.create(form.subject(), form.content());

        return "redirect:/question/list";
    }
}
