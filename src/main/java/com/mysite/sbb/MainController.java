package com.mysite.sbb;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public void index() {
        // 서버 콘솔에만 출력되고, 브라우저에는 아무것도 전달되지 않는다.
        System.out.println("index");
    }

    @GetMapping("/hello")
    public void hello() {
        System.out.println("hello");
    }
}
