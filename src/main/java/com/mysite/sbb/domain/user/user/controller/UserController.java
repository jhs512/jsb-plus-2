package com.mysite.sbb.domain.user.user.controller;

import com.mysite.sbb.domain.user.user.entity.SiteUser;
import com.mysite.sbb.domain.user.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

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

    // 화면만 보여준다. POST /user/login 처리는 스프링 시큐리티 필터가 담당한다.
    @GetMapping("/login")
    public String showLogin() {
        return "login_form";
    }

    // ===== 비밀번호 변경 (도전과제 3-14) =====
    public record PasswordChangeForm(
            @NotBlank(message = "01-currentPassword-현재 비밀번호를 입력해주세요.")
            String currentPassword,

            @NotBlank(message = "03-newPassword1-새 비밀번호를 입력해주세요.")
            @Size(min = 4, message = "04-newPassword1-새 비밀번호는 4자 이상이어야 합니다.")
            String newPassword1,

            @NotBlank(message = "05-newPassword2-새 비밀번호 확인을 입력해주세요.")
            String newPassword2
    ) {
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/password")
    public String showChangePassword(@ModelAttribute("form") PasswordChangeForm form) {
        return "password_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/password")
    @Transactional
    public String changePassword(
            @ModelAttribute("form") @Valid PasswordChangeForm form,
            BindingResult bindingResult,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        SiteUser user = userService.getUser(principal.getName());

        if (!bindingResult.hasFieldErrors("currentPassword") && !userService.matchesPassword(user, form.currentPassword())) {
            bindingResult.rejectValue("currentPassword", "mismatch", "02-currentPassword-현재 비밀번호가 올바르지 않습니다.");
        }

        if (form.newPassword1() != null && !form.newPassword1().equals(form.newPassword2())) {
            bindingResult.rejectValue("newPassword2", "passwordInCorrect", "06-newPassword2-새 비밀번호가 서로 일치하지 않습니다.");
        }

        if (bindingResult.hasErrors()) return "password_form";

        userService.changePassword(user, form.newPassword1());

        // 리다이렉트 후 한 번만 보여줄 메시지 (세션에 잠깐 저장되었다가 사라짐)
        redirectAttributes.addFlashAttribute("msg", "비밀번호가 변경되었습니다.");
        return "redirect:/user/password";
    }

    // ===== 비밀번호 찾기 (도전과제 3-14) =====
    public record PasswordResetForm(
            @NotBlank(message = "01-email-이메일을 입력해주세요.")
            @Email(message = "02-email-이메일 형식이 올바르지 않습니다.")
            String email
    ) {
    }

    @GetMapping("/password/reset")
    public String showResetPassword(@ModelAttribute("form") PasswordResetForm form) {
        return "password_reset_form";
    }

    @PostMapping("/password/reset")
    @Transactional
    public String resetPassword(
            @ModelAttribute("form") @Valid PasswordResetForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) return "password_reset_form";

        userService.resetPassword(form.email());

        redirectAttributes.addFlashAttribute("msg", "가입된 이메일이라면 임시 비밀번호를 보냈습니다. 메일을 확인해주세요.");
        return "redirect:/user/login";
    }
}
