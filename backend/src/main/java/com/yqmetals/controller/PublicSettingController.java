package com.yqmetals.controller;

import com.yqmetals.service.SettingService;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.Duration;

@RestController
@RequestMapping("/api/settings")
public class PublicSettingController {

    private static final List<String> PUBLIC_KEYS = List.of(
            "hero.image",
            "hero.title",
            "hero.subtitle",
            "company.phone",
            "company.address",
            "company.markets",
            "home.about.title",
            "home.about.p1",
            "home.about.p2",
            "home.why.title",
            "home.why.1.title", "home.why.1.desc",
            "home.why.2.title", "home.why.2.desc",
            "home.why.3.title", "home.why.3.desc",
            "home.why.4.title", "home.why.4.desc",
            "home.quality.image", "home.quality.title", "home.quality.desc",
            "home.faq.1.q", "home.faq.1.a",
            "home.faq.2.q", "home.faq.2.a",
            "home.faq.3.q", "home.faq.3.a",
            "home.faq.4.q", "home.faq.4.a",
            "home.cta.title", "home.cta.desc",
            "home.stats.years", "home.stats.years_label",
            "home.stats.area", "home.stats.area_label",
            "home.stats.countries", "home.stats.countries_label",
            "home.stats.cert", "home.stats.cert_label",
            "company.name", "company.trading.name",
            "company.certifications",
            "company.annual.capacity",
            "company.factory.area",
            "company.products"
    );

    private final SettingService settingService;

    public PublicSettingController(SettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping("/public")
    public ResponseEntity<Map<String, String>> publicSettings() {
        Map<String, String> all = settingService.getAll();
        Map<String, String> result = new HashMap<>();
        for (String key : PUBLIC_KEYS) {
            String value = all.get(key);
            if (value != null && !value.isBlank()) {
                result.put(key, value);
            }
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .cacheControl(CacheControl.maxAge(Duration.ofSeconds(60)).cachePublic())
                .body(result);
    }
}
