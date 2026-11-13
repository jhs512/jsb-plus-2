package com.mysite.sbb.domain.home.home.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {
    // 루트 URL로 들어오면 질문 목록으로 보낸다. (교재 2-08)
    // redirect: -> 302 응답 + Location 헤더 -> 브라우저가 그 주소로 다시 요청
    @GetMapping("/")
    public String index() {
        return "redirect:/question/list";
    }

    @GetMapping("/hello")
    @ResponseBody // 반환값이 그대로 HTTP 응답 본문이 된다.
    public String hello() {
        return "안녕하세요.";
    }
}
