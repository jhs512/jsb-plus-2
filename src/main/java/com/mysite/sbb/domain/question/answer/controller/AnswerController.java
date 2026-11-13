package com.mysite.sbb.domain.question.answer.controller;

import com.mysite.sbb.domain.question.answer.entity.Answer;
import com.mysite.sbb.domain.question.answer.service.AnswerService;
import com.mysite.sbb.domain.question.question.entity.Question;
import com.mysite.sbb.domain.question.question.service.QuestionService;
import com.mysite.sbb.domain.user.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Controller
@RequestMapping("/answer")
@RequiredArgsConstructor
public class AnswerController {
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;

    public record AnswerForm(
            @NotBlank(message = "01-content-내용은 필수항목입니다.")
            String content
    ) {
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    @Transactional // 조회 + 답변 추가를 하나의 트랜잭션으로 -> 메서드가 끝날 때 INSERT (14168 방식)
    public String createAnswer(
            @PathVariable int id,
            @ModelAttribute("answerForm") @Valid AnswerForm answerForm,
            BindingResult bindingResult,
            Model model,
            Principal principal
    ) {
        Question question = questionService.getQuestion(id);

        if (bindingResult.hasErrors()) {
            // 상세 화면을 다시 그리려면 question도 필요하다.
            model.addAttribute("question", question);
            return "question_detail";
        }

        answerService.create(question, userService.getUser(principal.getName()), answerForm.content());

        // 처리 후 화면을 직접 그리지 않고 상세 페이지로 다시 요청하게 한다. (POST-Redirect-GET)
        // -> 새로고침해도 답변이 중복 등록되지 않는다. (14168 28강)
        return "redirect:/question/detail/%d".formatted(id);
    }

    // 본인 답변이 아니면 403
    private Answer getAnswerForAuthor(int id, Principal principal) {
        Answer answer = answerService.getAnswer(id);
        if (!answer.isAuthor(userService.getUser(principal.getName()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
        return answer;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String showModify(@PathVariable int id, Principal principal, Model model) {
        Answer answer = getAnswerForAuthor(id, principal);
        model.addAttribute("answerForm", new AnswerForm(answer.getContent()));

        return "answer_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/modify/{id}")
    @Transactional
    public String modify(
            @PathVariable int id,
            @ModelAttribute("answerForm") @Valid AnswerForm answerForm,
            BindingResult bindingResult,
            Principal principal
    ) {
        Answer answer = getAnswerForAuthor(id, principal);

        if (bindingResult.hasErrors()) return "answer_form";

        answerService.modify(answer, answerForm.content());

        return "redirect:/question/detail/%d".formatted(answer.getQuestion().getId());
    }
}
