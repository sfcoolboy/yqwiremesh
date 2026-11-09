package com.yqmetals.controller;

import com.yqmetals.service.SettingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/settings")
@PreAuthorize("hasRole('ADMIN')")
public class SettingController {

    /** 管理端全量读取时脱敏，避免把 SMTP 密码暴露在接口响应里。 */
    private static final Set<String> SENSITIVE_KEYS = Set.of("mail.smtp.password", "mail.password");

    private final SettingService settingService;

    public SettingController(SettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping
    public Map<String, String> getAll() {
        return filterSensitive(settingService.getAll());
    }

    @PutMapping
    public Map<String, String> saveAll(@RequestBody Map<String, String> settings) {
        settings.forEach((key, value) -> {
            // 密码字段留空时跳过，防止页面刷新后把已存的密码冲掉。
            if (SENSITIVE_KEYS.contains(key) && (value == null || value.isBlank())) {
                return;
            }
            settingService.save(key, value == null ? "" : value.trim());
        });
        return filterSensitive(settingService.getAll());
    }

    private Map<String, String> filterSensitive(Map<String, String> settings) {
        return settings.entrySet().stream()
                .filter(e -> !SENSITIVE_KEYS.contains(e.getKey()))
                .collect(LinkedHashMap::new,
                        (map, e) -> map.put(e.getKey(), e.getValue()),
                        Map::putAll);
    }
}
