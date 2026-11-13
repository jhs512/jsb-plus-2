package com.mysite.sbb.domain.question.category.entity;

import com.mysite.sbb.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

// 질문 카테고리 (도전과제 3-14). 예) 질문답변, 자유게시판, 강좌
@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Category extends BaseEntity {
    @Column(unique = true)
    private String code; // URL에 쓰는 값. 예) qna

    private String name; // 화면에 보이는 이름. 예) 질문답변

    public Category(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
