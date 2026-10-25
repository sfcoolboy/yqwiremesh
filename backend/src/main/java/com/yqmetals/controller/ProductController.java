package com.yqmetals.controller;

import com.yqmetals.entity.Product;
import com.yqmetals.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> list() {
        return productService.findAllActive();
    }

    @GetMapping("/category/{categoryId}")
    public List<Product> byCategory(@PathVariable Long categoryId) {
        return productService.findByCategoryId(categoryId);
    }

    @GetMapping("/{id}")
    public Product get(@PathVariable Long id) {
        return productService.getById(id);
    }

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public Product create(@RequestBody Product p) {
        productService.save(p);
        return p;
    }

    @PutMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public Product update(@PathVariable Long id, @RequestBody Product p) {
        p.setId(id);
        productService.updateById(p);
        return p;
    }

    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        productService.removeById(id);
    }
}
