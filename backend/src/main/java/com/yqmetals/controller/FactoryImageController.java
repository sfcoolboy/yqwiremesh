package com.yqmetals.controller;

import com.yqmetals.entity.FactoryImage;
import com.yqmetals.service.FactoryImageService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FactoryImageController {

    private final FactoryImageService factoryImageService;

    public FactoryImageController(FactoryImageService factoryImageService) {
        this.factoryImageService = factoryImageService;
    }

    @GetMapping("/api/factory-images")
    public List<FactoryImage> publicList(
            @RequestParam(required = false) String type) {
        if (type != null) {
            return factoryImageService.findAll().stream()
                    .filter(img -> type.equals(img.getType()))
                    .collect(Collectors.toList());
        }
        return factoryImageService.findAll();
    }

    @GetMapping("/api/admin/factory-images")
    @PreAuthorize("hasRole('ADMIN')")
    public List<FactoryImage> adminList() {
        return factoryImageService.findAll();
    }

    @GetMapping("/api/home-images")
    public List<FactoryImage> publicHomeImages(
            @RequestParam(required = false) String subtype) {
        List<FactoryImage> all = factoryImageService.findAll();
        if (subtype == null) {
            return List.of();
        }
        if ("about".equals(subtype)) {
            return all.stream().filter(img -> "home-img".equals(img.getType()) && "about".equals(img.getPage()))
                    .collect(Collectors.toList());
        }
        if ("faq".equals(subtype)) {
            return all.stream().filter(img -> "home-img".equals(img.getType()) && "faq".equals(img.getPage()))
                    .collect(Collectors.toList());
        }
        if ("quality".equals(subtype)) {
            return all.stream().filter(img -> "home-img".equals(img.getType()) && "quality".equals(img.getPage()))
                    .collect(Collectors.toList());
        }
        return all.stream().filter(img -> "home-img".equals(img.getType()))
                .collect(Collectors.toList());
    }

    @GetMapping("/api/home-images/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public List<FactoryImage> adminHomeImages() {
        return factoryImageService.findAll().stream()
                .filter(img -> "home-img".equals(img.getType()))
                .collect(Collectors.toList());
    }

    @PostMapping("/api/admin/factory-images")
    @PreAuthorize("hasRole('ADMIN')")
    public FactoryImage create(@RequestBody FactoryImage image) {
        factoryImageService.save(image);
        return image;
    }

    @PutMapping("/api/admin/factory-images/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public FactoryImage update(@PathVariable("id") Long id, @RequestBody FactoryImage image) {
        image.setId(id);
        factoryImageService.updateById(image);
        return image;
    }

    @GetMapping("/api/factory-images/for-factory")
    public List<FactoryImage> forFactory() {
        return factoryImageService.findAll().stream()
                .filter(img -> "factory".equals(img.getType()))
                .filter(img -> Boolean.TRUE.equals(img.getShowOnFactory()))
                .collect(Collectors.toList());
    }

    @DeleteMapping("/api/admin/factory-images/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable("id") Long id) {
        factoryImageService.removeById(id);
    }
}

