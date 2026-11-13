package com.mysite.sbb.global.web;

import com.mysite.sbb.domain.question.category.entity.Category;
import com.mysite.sbb.domain.question.category.service.CategoryService;
import com.mysite.sbb.global.markdown.MarkdownService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

// 모든 컨트롤러의 모든 액션 메서드가 실행되기 전에 실행되어, 공통 템플릿 변수를 등록한다. (14168 19강)
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttribute {
    private final MarkdownService markdownService;
    private final CategoryService categoryService;

    // 템플릿에서 ${markdown.render(내용)}
    @ModelAttribute("markdown")
    public MarkdownService markdown() {
        return markdownService;
    }

    // 내비게이션 바, 질문 폼의 카테고리 목록
    @ModelAttribute("categories")
    public List<Category> categories() {
        return categoryService.getAll();
    }
}
