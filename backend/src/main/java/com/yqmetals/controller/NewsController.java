package com.yqmetals.controller;

import com.yqmetals.entity.News;
import com.yqmetals.service.NewsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public List<News> list() {
        return newsService.findPublished();
    }

    @GetMapping("/category/{category}")
    public List<News> byCategory(@PathVariable String category) {
        return newsService.findByCategory(category);
    }

    @GetMapping("/{id}")
    public News get(@PathVariable Long id) {
        return newsService.getById(id);
    }

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public News create(@RequestBody News n) {
        newsService.save(n);
        return n;
    }

    @PutMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public News update(@PathVariable Long id, @RequestBody News n) {
        n.setId(id);
        newsService.updateById(n);
        return n;
    }

    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        newsService.removeById(id);
    }
}
