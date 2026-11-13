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
import java.util.Objects;

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

    // 같은 id면 같은 엔티티로 본다. (14168 37, 38강)
    // -> question.getAnswers().remove(answer) 가 정확한 답변을 찾아 지울 수 있다.
    @Override
    public boolean equals(Object o) {
        if (o == this) return true; // 동치성(같은 객체)이면 바로 true
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return id == that.id;       // 동등성(같은 id)
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
