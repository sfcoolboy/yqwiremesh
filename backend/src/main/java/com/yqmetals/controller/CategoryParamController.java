package com.yqmetals.controller;

import com.yqmetals.entity.Category;
import com.yqmetals.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 类目参数管理接口
 * Category.specs 存储 JSON 数组，格式：[{"id":1,"name":"Mesh Size","unit":"mm"},{"id":2,"name":"Wire Diameter","unit":"mm"}]
 * Product.specs 存储 JSON 对象，格式：{"1":"50mm","2":"3mm"}
 */
@RestController
@RequestMapping("/api/categories/params")
public class CategoryParamController {

    private final CategoryService categoryService;

    public CategoryParamController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /** 获取所有产品类目的参数定义（含参数列表） */
    @GetMapping
    public List<Category> listAll() {
        return categoryService.findByType("PRODUCT");
    }

    /** 获取指定类目的参数定义（JSON 字符串） */
    @GetMapping("/{id}")
    public Category get(@PathVariable Long id) {
        Category cat = categoryService.getById(id);
        return (cat != null) ? cat : null;
    }

    /** 保存类目的参数定义 */
    @PutMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public Category save(@PathVariable Long id, @RequestBody Category update) {
        Category cat = categoryService.getById(id);
        if (cat == null) throw new RuntimeException("类目不存在");
        cat.setSpecs(update.getSpecs());
        categoryService.updateById(cat);
        return cat;
    }
}
