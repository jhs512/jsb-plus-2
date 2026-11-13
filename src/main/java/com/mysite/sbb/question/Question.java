package com.mysite.sbb.question;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity // 이 클래스를 기준으로 question 테이블이 만들어진다.
@Getter
@Setter
public class Question {
    @Id // 기본키(PK)
    @GeneratedValue(strategy = IDENTITY) // DB가 1, 2, 3 ... 자동 증가
    private int id;

    @Column(length = 200) // varchar(200)
    private String subject;

    @Column(columnDefinition = "TEXT") // 길이 제한 없는 텍스트
    private String content;

    private LocalDateTime createDate; // 컬럼명은 create_date (카멜케이스 -> 스네이크케이스)
}
