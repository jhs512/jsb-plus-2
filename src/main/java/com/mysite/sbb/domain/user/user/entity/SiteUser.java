package com.mysite.sbb.domain.user.user.entity;

import com.mysite.sbb.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

// user는 많은 DB에서 예약어라서 테이블 이름 충돌을 피하려고 SiteUser로 짓는다. (교재 3-06)
@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class SiteUser extends BaseEntity {
    @Column(unique = true) // 같은 아이디로 두 번 가입할 수 없다. (DB 유니크 제약)
    private String username;

    private String password; // 암호화된 값만 저장한다.

    @Column(unique = true)
    private String email;

    public SiteUser(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
