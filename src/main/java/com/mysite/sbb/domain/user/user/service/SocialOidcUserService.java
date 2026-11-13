package com.mysite.sbb.domain.user.user.service;

import com.mysite.sbb.domain.user.user.entity.SiteUser;
import com.mysite.sbb.domain.user.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// scope에 openid가 있으면(구글 기본) 시큐리티는 OAuth2UserService가 아니라 OIDC용 OidcUserService를 호출한다.
// 실제 구글 로그인 테스트에서 회원이 만들어지지 않는 버그로 발견 -> OIDC 경로에서도 같은 방식으로 SiteUser와 연결한다.
@Service
@RequiredArgsConstructor
public class SocialOidcUserService extends OidcUserService {
    private final UserService userService;

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest); // ID 토큰 검증 + 사용자 정보

        String provider = userRequest.getClientRegistration().getRegistrationId(); // google
        SiteUser siteUser = userService.findOrCreateSocialUser(provider, oidcUser.getSubject(), oidcUser.getEmail());

        return new SiteOidcUser(oidcUser, siteUser.getUsername());
    }

    // principal.getName()이 우리 회원의 username을 돌려주도록 이름만 바꾼 OIDC 사용자
    static class SiteOidcUser extends DefaultOidcUser {
        private final String username;

        SiteOidcUser(OidcUser source, String username) {
            super(List.of(new SimpleGrantedAuthority(UserRole.USER.getValue())), source.getIdToken(), source.getUserInfo());
            this.username = username;
        }

        @Override
        public String getName() {
            return username;
        }
    }
}
