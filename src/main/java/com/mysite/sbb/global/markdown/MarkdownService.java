package com.mysite.sbb.global.markdown;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Component;

// 마크다운 문자열 -> HTML 문자열 (교재 3-12의 CommonUtil)
// 교재처럼 템플릿에서 ${@빈이름.메서드()}로 부르면 Thymeleaf 3.1.5에서는 보안상 막힌다.
// -> GlobalModelAttribute가 모든 화면에 "markdown"이라는 이름으로 넣어 준다.
@Component
public class MarkdownService {
    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    public String render(String markdown) {
        if (markdown == null) return "";
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }
}
