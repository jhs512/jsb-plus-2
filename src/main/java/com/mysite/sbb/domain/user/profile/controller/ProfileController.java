package com.mysite.sbb.domain.user.profile.controller;

import com.mysite.sbb.domain.question.answer.repository.AnswerRepository;
import com.mysite.sbb.domain.question.comment.repository.CommentRepository;
import com.mysite.sbb.domain.question.question.repository.QuestionRepository;
import com.mysite.sbb.domain.user.user.entity.SiteUser;
import com.mysite.sbb.domain.user.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

// 프로필 (도전과제 3-14): 기본 정보 + 내가 쓴 질문, 답변, 댓글
// 단순 조회 화면이라 별도 서비스 없이 리포지터리를 바로 사용한다.
@Controller
@RequiredArgsConstructor
public class ProfileController {
    private final UserService userService;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CommentRepository commentRepository;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user/profile")
    @Transactional(readOnly = true) // 조회 전용 트랜잭션: 변경 감지를 하지 않아 가볍다.
    public String profile(Principal principal, Model model) {
        SiteUser user = userService.getUser(principal.getName());

        model.addAttribute("user", user);
        model.addAttribute("questionCount", questionRepository.countByAuthor(user));
        model.addAttribute("answerCount", answerRepository.countByAuthor(user));
        model.addAttribute("commentCount", commentRepository.countByAuthor(user));
        model.addAttribute("questions", questionRepository.findTop10ByAuthorOrderByCreateDateDesc(user));
        model.addAttribute("answers", answerRepository.findTop10ByAuthorOrderByCreateDateDesc(user));
        model.addAttribute("comments", commentRepository.findTop10ByAuthorOrderByCreateDateDesc(user));

        return "profile";
    }
}
