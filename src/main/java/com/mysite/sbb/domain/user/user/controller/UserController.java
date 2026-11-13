package com.mysite.sbb.domain.user.user.controller;

import com.mysite.sbb.domain.user.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    public record UserCreateForm(
            @NotBlank(message = "01-username-사용자ID는 필수항목입니다.")
            @Size(min = 3, max = 25, message = "02-username-사용자ID는 3자 이상 25자 이하로 입력해주세요.")
            String username,

            @NotBlank(message = "04-password1-비밀번호는 필수항목입니다.")
            String password1,

            @NotBlank(message = "05-password2-비밀번호 확인은 필수항목입니다.")
            String password2,

            @NotBlank(message = "07-email-이메일은 필수항목입니다.")
            @Email(message = "08-email-이메일 형식이 올바르지 않습니다.")
            String email
    ) {
    }

    @GetMapping("/signup")
    public String showSignup(@ModelAttribute("form") UserCreateForm form) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(
            @ModelAttribute("form") @Valid UserCreateForm form,
            BindingResult bindingResult
    ) {
        // 어노테이션으로 표현하기 어려운 검증은 직접 하고, 같은 형식의 메시지로 에러를 추가한다.
        if (form.password1() != null && !form.password1().equals(form.password2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "06-password2-2개의 비밀번호가 일치하지 않습니다.");
        }

        // 교재는 저장 시 예외(DataIntegrityViolationException)를 잡지만, 저장 전에 미리 확인해 필드별 메시지를 준다.
        if (!bindingResult.hasFieldErrors("username") && userService.existsByUsername(form.username())) {
            bindingResult.rejectValue("username", "duplicate", "03-username-이미 사용 중인 사용자ID입니다.");
        }

        if (!bindingResult.hasFieldErrors("email") && userService.existsByEmail(form.email())) {
            bindingResult.rejectValue("email", "duplicate", "09-email-이미 사용 중인 이메일입니다.");
        }

        if (bindingResult.hasErrors()) return "signup_form";

        userService.create(form.username(), form.email(), form.password1());

        return "redirect:/";
    }
}
