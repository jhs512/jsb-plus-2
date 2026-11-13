package com.mysite.sbb.global.jpa.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

// 모든 엔티티가 공통으로 가지는 id, 생성일, 수정일 (14168 2강)
@MappedSuperclass // 이 클래스 자체는 테이블이 되지 않고, 필드만 자식 엔티티 테이블에 들어간다.
@EntityListeners(AuditingEntityListener.class) // 저장/수정 시점을 감지해서 날짜를 자동으로 채운다.
@Getter
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;

    @CreatedDate // INSERT 될 때 자동 기록
    private LocalDateTime createDate;

    @LastModifiedDate // INSERT, UPDATE 될 때 자동 기록
    private LocalDateTime modifyDate;
}
