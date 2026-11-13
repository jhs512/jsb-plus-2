package com.mysite.sbb.global.markdown;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// 스프링 없이 순수 자바 객체로 테스트 (빠르다)
class MarkdownServiceTest {
    private final MarkdownService markdownService = new MarkdownService();

    @Test
    @DisplayName("마크다운 -> HTML")
    void t1() {
        String html = markdownService.render("# 제목\n\n**굵게**\n\n* 항목");

        assertThat(html).contains("<h1>제목</h1>", "<strong>굵게</strong>", "<li>항목</li>");
    }
}
