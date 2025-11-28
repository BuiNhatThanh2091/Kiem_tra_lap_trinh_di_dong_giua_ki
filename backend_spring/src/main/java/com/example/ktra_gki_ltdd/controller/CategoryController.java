package com.example.ktra_gki_ltdd.controller;

import com.example.ktra_gki_ltdd.entity.Category;
import com.example.ktra_gki_ltdd.repository.CategoryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")   // cho phép mọi origin, để app mobile gọi dễ
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // GET /api/categories
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
