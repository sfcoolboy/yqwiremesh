package com.yqmetals.controller;

import com.yqmetals.entity.News;
import com.yqmetals.entity.Product;
import com.yqmetals.service.NewsService;
import com.yqmetals.service.ProductService;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.Duration;

@RestController
@RequestMapping("")
public class SitemapController {

    @Value("${app.base-url}")
    private String baseUrl;

    private final NewsService newsService;
    private final ProductService productService;

    public SitemapController(NewsService newsService, ProductService productService) {
        this.newsService = newsService;
        this.productService = productService;
    }

    private static String escapeXml(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    @GetMapping(value = "/sitemap.xml", produces = "application/xml;charset=UTF-8")
    public ResponseEntity<String> sitemap() {
        List<News> newsList = newsService.findPublished();
        List<Product> productList = productService.findAllActive();

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"\n");
        sb.append("        xmlns:image=\"http://www.google.com/schemas/sitemap-image/1.1\">\n");

        // Static pages with priorities
        sb.append("  <url><loc>").append(baseUrl).append("/</loc><changefreq>weekly</changefreq><priority>1.0</priority></url>\n");
        sb.append("  <url><loc>").append(baseUrl).append("/products</loc><changefreq>weekly</changefreq><priority>0.9</priority></url>\n");
        sb.append("  <url><loc>").append(baseUrl).append("/about</loc><changefreq>monthly</changefreq><priority>0.8</priority></url>\n");
        sb.append("  <url><loc>").append(baseUrl).append("/factory</loc><changefreq>monthly</changefreq><priority>0.7</priority></url>\n");
        sb.append("  <url><loc>").append(baseUrl).append("/news</loc><changefreq>weekly</changefreq><priority>0.7</priority></url>\n");

        // Product detail pages
        for (Product p : productList) {
            LocalDateTime lastmod = p.getUpdateTime() != null ? p.getUpdateTime() : p.getCreateTime();
            sb.append(String.format("  <url><loc>%s/product/%d</loc><lastmod>%s</lastmod><changefreq>monthly</changefreq><priority>0.6</priority>", baseUrl, p.getId(), lastmod.format(DateTimeFormatter.ISO_LOCAL_DATE)));
            if (p.getImageUrl() != null) {
                sb.append(String.format("<image:image><image:loc>%s%s</image:loc><image:caption>%s</image:caption></image:image>", baseUrl, escapeXml(p.getImageUrl()), escapeXml(p.getName())));
            }
            sb.append("</url>\n");
        }

        // News detail pages
        for (News n : newsList) {
            sb.append(String.format("  <url><loc>%s/news/%d</loc><lastmod>%s</lastmod><changefreq>monthly</changefreq><priority>0.5</priority>", baseUrl, n.getId(), n.getPublishDate().format(DateTimeFormatter.ISO_LOCAL_DATE)));
            if (n.getCoverImageUrl() != null) {
                sb.append(String.format("<image:image><image:loc>%s%s</image:loc><image:caption>%s</image:caption></image:image>", baseUrl, escapeXml(n.getCoverImageUrl()), escapeXml(n.getTitle())));
            }
            sb.append("</url>\n");
        }

        sb.append("</urlset>");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .cacheControl(CacheControl.maxAge(Duration.ofHours(1)).cachePublic())
                .body(sb.toString());
    }
}
