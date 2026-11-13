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
    // th:utext는 이스케이프를 하지 않으므로(14168 16강), 입력에 섞인 HTML은 여기서 막아야 한다.
    // escapeHtml: <script> 같은 원본 HTML을 &lt;script&gt; 문자로 바꿔 보여준다.
    // sanitizeUrls: [링크](javascript:...) 같은 위험한 URL을 무력화한다.
    private final HtmlRenderer renderer = HtmlRenderer.builder()
            .escapeHtml(true)
            .sanitizeUrls(true)
            .build();

    public String render(String markdown) {
        if (markdown == null) return "";
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }
}
