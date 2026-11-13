package com.mysite.sbb.domain.user.user.service;

import com.mysite.sbb.domain.user.user.entity.SiteUser;
import com.mysite.sbb.domain.user.user.repository.UserRepository;
import com.mysite.sbb.global.exception.DataNotFoundException;
import com.mysite.sbb.global.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    private static final String TEMP_PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    private final SecureRandom random = new SecureRandom();

    public SiteUser create(String username, String email, String password) {
        SiteUser user = new SiteUser(username, passwordEncoder.encode(password), email);
        return userRepository.save(user);
    }

    public boolean matchesPassword(SiteUser user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public void changePassword(SiteUser user, String newRawPassword) {
        user.changePassword(passwordEncoder.encode(newRawPassword)); // 트랜잭션 안이면 더티체킹으로 UPDATE
    }

    // 소셜 로그인 사용자 (도전과제 3-14)
    // 아이디는 "제공자_제공자의사용자ID" (예: google_1234567890), 비밀번호는 쓸 일이 없으므로 무작위 값
    public SiteUser findOrCreateSocialUser(String provider, String providerUserId, String email) {
        String username = provider + "_" + providerUserId;

        return userRepository.findByUsername(username).orElseGet(() -> {
            // 같은 이메일로 이미 일반 가입한 계정이 있으면 unique 충돌이 나므로 이메일은 비워 둔다.
            String safeEmail = (email == null || userRepository.existsByEmail(email)) ? null : email;
            return create(username, safeEmail, UUID.randomUUID().toString());
        });
    }

    // 비밀번호 찾기 (도전과제 3-14): 임시 비밀번호로 바꾸고 가입한 이메일로 보낸다.
    // 이메일이 없어도 예외를 던지지 않는다. -> 화면에서 가입 여부를 알 수 없게 (계정 존재 여부 노출 방지)
    public void resetPassword(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String tempPassword = createTempPassword();
            changePassword(user, tempPassword);
            mailService.send(
                    user.getEmail(),
                    "[SBB] 임시 비밀번호 안내",
                    "%s님의 임시 비밀번호는 %s 입니다. 로그인 후 비밀번호를 변경해주세요.".formatted(user.getUsername(), tempPassword)
            );
        });
    }

    private String createTempPassword() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) sb.append(TEMP_PASSWORD_CHARS.charAt(random.nextInt(TEMP_PASSWORD_CHARS.length())));
        return sb.toString();
    }

    public SiteUser getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new DataNotFoundException("siteuser not found"));
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
