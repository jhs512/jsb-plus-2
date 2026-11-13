package com.mysite.sbb.global.web;

import com.mysite.sbb.global.markdown.MarkdownService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

// 모든 컨트롤러의 모든 액션 메서드가 실행되기 전에 실행되어, 공통 템플릿 변수를 등록한다. (14168 19강)
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttribute {
    private final MarkdownService markdownService;

    // 템플릿에서 ${markdown.render(내용)}
    @ModelAttribute("markdown")
    public MarkdownService markdown() {
        return markdownService;
    }
}
