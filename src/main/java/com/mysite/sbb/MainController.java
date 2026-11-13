package com.mysite.sbb;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {
    @GetMapping("/")
    @ResponseBody // 반환값이 그대로 HTTP 응답 본문이 된다.
    public String index() {
        return "SBB";
    }

    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return "안녕하세요.";
    }
}
