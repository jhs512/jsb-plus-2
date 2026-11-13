package com.mysite.sbb.domain.user.user.service;

import com.mysite.sbb.domain.user.user.entity.SiteUser;
import com.mysite.sbb.domain.user.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 소셜 로그인 성공 후, 제공자(구글 등)에게 받은 사용자 정보를 우리 회원(SiteUser)과 연결한다. (도전과제 3-14)
@Service
@RequiredArgsConstructor
public class SocialOAuth2UserService extends DefaultOAuth2UserService {
    private final UserService userService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest); // 제공자의 사용자 정보 API 호출

        String provider = userRequest.getClientRegistration().getRegistrationId(); // google
        String idAttribute = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName(); // 구글은 "sub"
        String providerUserId = String.valueOf(oAuth2User.getAttributes().get(idAttribute));
        String email = (String) oAuth2User.getAttributes().get("email");

        SiteUser siteUser = userService.findOrCreateSocialUser(provider, providerUserId, email);

        // principal.getName()이 우리 회원의 username이 되도록 "username" 속성을 이름 키로 사용한다.
        // -> 기존 코드(userService.getUser(principal.getName()))를 그대로 쓸 수 있다.
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("username", siteUser.getUsername());

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(UserRole.USER.getValue())),
                attributes,
                "username"
        );
    }
}
