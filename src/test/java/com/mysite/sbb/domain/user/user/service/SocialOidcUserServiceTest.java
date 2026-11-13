package com.mysite.sbb.domain.user.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SocialOidcUserServiceTest {
    @Test
    @DisplayName("OIDC 로그인 사용자의 이름(principal.getName())은 구글 sub가 아니라 우리 회원 username")
    void t1() {
        OidcIdToken idToken = new OidcIdToken("token", Instant.now(), Instant.now().plusSeconds(60),
                Map.of("sub", "1234567890", "email", "social@test.com"));
        DefaultOidcUser google = new DefaultOidcUser(List.of(), idToken);
        assertThat(google.getName()).isEqualTo("1234567890"); // 기본값은 sub -> 버그의 원인

        var siteUser = new SocialOidcUserService.SiteOidcUser(google, "google_1234567890");

        assertThat(siteUser.getName()).isEqualTo("google_1234567890");
        assertThat(siteUser.getEmail()).isEqualTo("social@test.com");
        assertThat(siteUser.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");
    }
}
