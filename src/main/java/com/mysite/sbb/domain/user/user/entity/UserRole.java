package com.mysite.sbb.domain.user.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 권한. 스프링 시큐리티는 "ROLE_" 접두어가 붙은 문자열을 역할로 인식한다. (교재 3-07)
@Getter
@RequiredArgsConstructor
public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private final String value;
}
