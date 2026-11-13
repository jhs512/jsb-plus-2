package com.mysite.sbb.domain.user.user.service;

import com.mysite.sbb.domain.user.user.entity.SiteUser;
import com.mysite.sbb.domain.user.user.entity.UserRole;
import com.mysite.sbb.domain.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

// 로그인 폼이 전송되면 시큐리티가 이 메서드로 사용자를 찾고,
// 반환된 UserDetails의 비밀번호(해시)와 입력한 비밀번호를 PasswordEncoder로 비교한다. (교재 3-07)
@Service
@RequiredArgsConstructor
public class UserSecurityService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SiteUser siteUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        UserRole role = "admin".equals(username) ? UserRole.ADMIN : UserRole.USER;

        return new User(
                siteUser.getUsername(),
                siteUser.getPassword(),
                List.of(new SimpleGrantedAuthority(role.getValue()))
        );
    }
}
