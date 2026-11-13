package com.mysite.sbb.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// 이 예외가 컨트롤러 밖으로 던져지면 스프링이 404 응답으로 바꿔 준다.
// 기본 예외(NoSuchElementException: No value present)보다 원인이 분명하다. (14163 35강)
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "entity not found")
public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException(String message) {
        super(message);
    }
}
