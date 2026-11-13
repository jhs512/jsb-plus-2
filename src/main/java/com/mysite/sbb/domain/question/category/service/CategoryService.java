package com.mysite.sbb.domain.question.category.service;

import com.mysite.sbb.domain.question.category.entity.Category;
import com.mysite.sbb.domain.question.category.repository.CategoryRepository;
import com.mysite.sbb.global.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category create(String code, String name) {
        return categoryRepository.save(new Category(code, name));
    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category getByCode(String code) {
        return categoryRepository.findByCode(code)
                .orElseThrow(() -> new DataNotFoundException("category not found"));
    }
}
