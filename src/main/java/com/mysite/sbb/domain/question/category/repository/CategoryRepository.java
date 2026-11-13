package com.mysite.sbb.domain.question.category.repository;

import com.mysite.sbb.domain.question.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByCode(String code);
}
