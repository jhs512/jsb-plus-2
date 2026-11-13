package com.mysite.sbb.domain.question.question.controller;

import com.mysite.sbb.domain.question.answer.controller.AnswerController.AnswerForm;
import com.mysite.sbb.domain.question.answer.service.AnswerService;
import com.mysite.sbb.domain.question.question.entity.Question;
import com.mysite.sbb.domain.question.question.service.QuestionService;
import com.mysite.sbb.domain.user.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Controller
@RequestMapping("/question") // 이 컨트롤러의 모든 URL 앞에 /question 이 붙는다. (교재 2-11)
@RequiredArgsConstructor // final 필드를 받는 생성자를 만들어 준다 -> 생성자 주입
public class QuestionController {
    private final QuestionService questionService;
    private final UserService userService;
    private final AnswerService answerService;

    // @ResponseBody가 없으므로 반환값 "question_list"는 템플릿 이름이 된다.
    // -> templates/question_list.html 을 렌더링한 HTML이 응답 본문이 된다. (14163 28강)
    @GetMapping("/list")
    public String list(
            Model model,
            @RequestParam(defaultValue = "0") int page, // /question/list?page=2
            @RequestParam(defaultValue = "") String kw // /question/list?kw=스프링
    ) {
        Page<Question> paging = questionService.getList(page, kw);
        model.addAttribute("paging", paging); // 목록(paging.content)과 페이지 정보(총 페이지 수 등)를 함께 전달
        model.addAttribute("kw", kw); // 검색창에 검색어를 유지하기 위해

        return "question_list";
    }

    // {id} 자리의 값이 @PathVariable int id 로 들어온다. 예) /question/detail/2 -> id = 2
    @GetMapping("/detail/{id}")
    public String detail(
            Model model,
            @PathVariable int id,
            @ModelAttribute("answerForm") AnswerForm answerForm, // 답변 폼(th:object)에 쓸 빈 객체
            @RequestParam(defaultValue = "0") int answerPage,
            @RequestParam(defaultValue = "latest") String answerSort
    ) {
        Question question = questionService.getQuestion(id);
        model.addAttribute("question", question);
        // 답변은 question.answers 전체 대신 페이지 단위로 (도전과제: 답변 페이징과 정렬)
        model.addAttribute("answerPaging", answerService.getList(question, answerPage, answerSort));
        model.addAttribute("answerSort", answerSort);

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
    // 로그인하지 않은 사용자가 접근하면 로그인 페이지로 보낸다. (교재 3-08)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String showCreate(@ModelAttribute("form") QuestionForm form) {
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String create(
            @ModelAttribute("form") @Valid QuestionForm form, // 요청 파라미터 -> QuestionForm, 그리고 검증
            BindingResult bindingResult, // 검증 결과. 꼭 @Valid 바로 뒤에 와야 한다. (14168 13강)
            Principal principal // 현재 로그인한 사용자 정보 (principal.getName() = username)
    ) {
        // 검증 실패: 예외 대신 폼을 다시 보여준다. 입력값과 에러는 form, bindingResult에 담겨 있다.
        if (bindingResult.hasErrors()) return "question_form";

        questionService.create(userService.getUser(principal.getName()), form.subject(), form.content());

        return "redirect:/question/list";
    }

    // 본인 글이 아니면 403 Forbidden. 교재는 400(BAD_REQUEST)이지만 "권한 없음"은 403이 의미상 정확하다.
    private Question getQuestionForAuthor(int id, Principal principal) {
        Question question = questionService.getQuestion(id);
        if (!question.isAuthor(userService.getUser(principal.getName()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
        return question;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String showModify(@PathVariable int id, Principal principal, Model model) {
        Question question = getQuestionForAuthor(id, principal);

        // 등록 폼(question_form.html)을 재사용한다. 기존 값을 채운 폼 객체 + PUT 방식으로 전송하라는 표시
        model.addAttribute("form", new QuestionForm(question.getSubject(), question.getContent()));
        model.addAttribute("method", "PUT");

        return "question_form";
    }

    // 수정은 PUT (교재는 POST /question/modify/{id}) - PLAN 결정 2
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/modify/{id}")
    @Transactional // 메서드가 끝날 때 더티체킹으로 UPDATE
    public String modify(
            @PathVariable int id,
            @ModelAttribute("form") @Valid QuestionForm form,
            BindingResult bindingResult,
            Principal principal,
            Model model
    ) {
        Question question = getQuestionForAuthor(id, principal);

        if (bindingResult.hasErrors()) {
            model.addAttribute("method", "PUT");
            return "question_form";
        }

        questionService.modify(question, form.subject(), form.content());

        return "redirect:/question/detail/%d".formatted(id);
    }

    // 삭제는 DELETE (교재는 GET /question/delete/{id}) - PLAN 결정 2
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete/{id}")
    @Transactional
    public String delete(@PathVariable int id, Principal principal) {
        Question question = getQuestionForAuthor(id, principal);
        questionService.delete(question);

        return "redirect:/";
    }

    // 추천은 데이터를 바꾸므로 POST (교재는 GET /question/vote/{id}) - PLAN 결정 2
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/vote/{id}")
    @Transactional
    public String vote(@PathVariable int id, Principal principal) {
        Question question = questionService.getQuestion(id);
        question.vote(userService.getUser(principal.getName()));

        return "redirect:/question/detail/%d".formatted(id);
    }
}
