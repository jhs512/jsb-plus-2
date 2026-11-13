package com.mysite.sbb.global.security;

import com.mysite.sbb.domain.user.user.service.SocialOAuth2UserService;
import com.mysite.sbb.domain.user.user.service.SocialOidcUserService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

// 시큐리티는 컨트롤러보다 앞단(필터)에서 동작한다.
// 설정이 없으면 기본값: 1) 인증 안 된 요청은 모두 거절(로그인 페이지로) 2) POST/PUT/DELETE는 CSRF 토큰 검사 (14168 46강)
// 교재의 AntPathRequestMatcher는 Spring Security 7(Boot 4)에서 제거되어 문자열 패턴을 사용한다.
@Configuration
@EnableMethodSecurity // 컨트롤러 메서드의 @PreAuthorize 동작
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            ObjectProvider<ClientRegistrationRepository> clientRegistrations,
            SocialOAuth2UserService socialOAuth2UserService,
            SocialOidcUserService socialOidcUserService
    ) {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/favicon.ico").permitAll()
                        .requestMatchers("/resource/**").permitAll() // 정적 파일은 한 줄로 허용 (14168 46강)
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().permitAll() // 지금은 모두 허용, 로그인 기능 이후 필요한 곳만 막는다.
                )
                // H2 콘솔은 iframe을 쓰므로 같은 출처의 iframe은 허용
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                // H2 콘솔의 폼에는 CSRF 토큰이 없으므로 검사 제외
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                )
                // 로그인 폼은 우리가 만든 /user/login 화면을 사용. 처리(POST /user/login)는 시큐리티가 한다.
                .formLogin(formLogin -> formLogin
                        .loginPage("/user/login")
                        .defaultSuccessUrl("/")
                )
                // 로그아웃은 POST /user/logout (CSRF 보호를 위해 GET이 아닌 POST, 교재와 다름)
                .logout(logout -> logout
                        .logoutUrl("/user/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                );

        // 소셜 로그인: client-id 설정(application-secret.yaml)이 있을 때만 켠다. 없으면 일반 로그인만 동작
        if (clientRegistrations.getIfAvailable() != null) {
            http.oauth2Login(oauth2 -> oauth2
                    .loginPage("/user/login")
                    .defaultSuccessUrl("/")
                    .userInfoEndpoint(userInfo -> userInfo
                            .userService(socialOAuth2UserService)    // OAuth2 (scope에 openid 없음)
                            .oidcUserService(socialOidcUserService)  // OIDC (scope에 openid 있음, 구글 기본)
                    )
            );
        }

        return http.build();
    }

    // 비밀번호는 원문 대신 BCrypt 해시로 저장한다. 같은 비밀번호도 매번 다른 값(salt)으로 저장된다.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
