package com.mysite.sbb.domain.question.comment.controller;

import com.mysite.sbb.domain.question.answer.entity.Answer;
import com.mysite.sbb.domain.question.answer.service.AnswerService;
import com.mysite.sbb.domain.question.comment.entity.Comment;
import com.mysite.sbb.domain.question.comment.service.CommentService;
import com.mysite.sbb.domain.question.question.service.QuestionService;
import com.mysite.sbb.domain.user.user.entity.SiteUser;
import com.mysite.sbb.domain.user.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

// 댓글 (도전과제 3-14, 14168 33~39강 PostComment 방식)
@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final CommentService commentService;
    private final UserService userService;

    // 클라이언트(JS)에서 먼저 검사하지만, 서버 검사는 반드시 한다. (14168 35, 36강)
    // BindingResult가 없으므로 검증 실패 시 400 응답 -> JS 검사를 우회한 비정상 요청만 여기에 걸린다.
    public record CommentForm(
            @NotBlank
            @Size(max = 500)
            String content
    ) {
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/question/{id}")
    @Transactional
    public String createForQuestion(@PathVariable int id, @Valid CommentForm form, Principal principal) {
        SiteUser author = userService.getUser(principal.getName());
        questionService.getQuestion(id).addComment(author, form.content());

        return "redirect:/question/detail/%d".formatted(id);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/answer/{id}")
    @Transactional
    public String createForAnswer(@PathVariable int id, @Valid CommentForm form, Principal principal) {
        SiteUser author = userService.getUser(principal.getName());
        Answer answer = answerService.getAnswer(id);
        answer.addComment(author, form.content());

        return "redirect:/question/detail/%d#answer_%d".formatted(answer.getQuestion().getId(), id);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete/{id}")
    @Transactional
    public String delete(@PathVariable int id, Principal principal) {
        Comment comment = commentService.getComment(id);
        if (!comment.isAuthor(userService.getUser(principal.getName()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
        int questionId = comment.getRootQuestionId();

        commentService.delete(comment);

        return "redirect:/question/detail/%d".formatted(questionId);
    }
}
