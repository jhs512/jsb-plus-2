package com.mysite.sbb.domain.user.user.service;

import com.mysite.sbb.domain.user.user.entity.SiteUser;
import com.mysite.sbb.domain.user.user.repository.UserRepository;
import com.mysite.sbb.global.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
