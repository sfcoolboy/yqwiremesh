package com.yqmetals.controller;

import com.yqmetals.entity.Category;
import com.yqmetals.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/tree")
    public List<Category> tree() {
        return categoryService.findTree();
    }

    @GetMapping("/list/{type}")
    public List<Category> listByType(@PathVariable String type) {
        return categoryService.findByType(type);
    }

    @GetMapping("/{parentId}/children")
    public List<Category> children(@PathVariable Long parentId) {
        return categoryService.findByParentId(parentId);
    }

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public Category create(@RequestBody Category c) {
        categoryService.save(c);
        return c;
    }

    @PutMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public Category update(@PathVariable Long id, @RequestBody Category c) {
        c.setId(id);
        categoryService.updateById(c);
        return c;
    }

    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        categoryService.removeById(id);
    }
}
