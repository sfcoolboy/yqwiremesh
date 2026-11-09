package com.yqmetals.controller;

import com.yqmetals.entity.FactoryImage;
import com.yqmetals.service.FactoryImageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/page-heroes")
public class PageHeroController {

    private final FactoryImageService factoryImageService;

    public PageHeroController(FactoryImageService factoryImageService) {
        this.factoryImageService = factoryImageService;
    }

    @GetMapping
    public List<FactoryImage> list() {
        return factoryImageService.findAll().stream()
                .filter(img -> "page-hero".equals(img.getType()) || "home-img".equals(img.getType()))
                .collect(Collectors.toList());
    }

    @GetMapping("/home")
    public FactoryImage getHome() {
        return factoryImageService.findAll().stream()
                .filter(img -> "page-hero".equals(img.getType()) && "home".equals(img.getPage()))
                .findFirst().orElse(null);
    }

    @GetMapping("/products")
    public FactoryImage getProducts() {
        return factoryImageService.findAll().stream()
                .filter(img -> "page-hero".equals(img.getType()) && "products".equals(img.getPage()))
                .findFirst().orElse(null);
    }

    @GetMapping("/about")
    public FactoryImage getAbout() {
        return factoryImageService.findAll().stream()
                .filter(img -> "page-hero".equals(img.getType()) && "about".equals(img.getPage()))
                .findFirst().orElse(null);
    }

    @GetMapping("/news")
    public FactoryImage getNews() {
        return factoryImageService.findAll().stream()
                .filter(img -> "page-hero".equals(img.getType()) && "news".equals(img.getPage()))
                .findFirst().orElse(null);
    }

    @GetMapping("/factory")
    public FactoryImage getFactory() {
        return factoryImageService.findAll().stream()
                .filter(img -> "page-hero".equals(img.getType()) && "factory".equals(img.getPage()))
                .findFirst().orElse(null);
    }
}