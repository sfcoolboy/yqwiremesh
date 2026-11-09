package com.yqmetals.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yqmetals.entity.Category;
import com.yqmetals.entity.Param;
import com.yqmetals.security.AuditLogInterceptor;
import com.yqmetals.service.AuditLogService;
import com.yqmetals.service.CategoryService;
import com.yqmetals.service.ParamService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/api/params")
@PreAuthorize("hasRole('ADMIN')")
public class ParamController {

    private final ParamService paramService;
    private final CategoryService categoryService;
    private final ObjectMapper objectMapper;
    private final AuditLogService auditLogService;

    public ParamController(
            ParamService paramService,
            CategoryService categoryService,
            ObjectMapper objectMapper,
            AuditLogService auditLogService) {
        this.paramService = paramService;
        this.categoryService = categoryService;
        this.objectMapper = objectMapper;
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public List<Param> listAll() {
        return paramService.listAll();
    }

    @PostMapping
    public Param create(@RequestBody Param param, HttpServletRequest request) {
        if (param.getEnabled() == null) {
            param.setEnabled(true);
        }
        paramService.save(param);
        try {
            auditLogService.record(getUsername(request), "创建产品参数", "param:" + param.getId(),
                    "创建参数「" + param.getName() + "」", clientIp(request), request.getHeader("User-Agent"), true);
        } catch (Exception ignored) {}
        return param;
    }

    @PutMapping("/{id}")
    public Param update(@PathVariable Long id, @RequestBody Param param, HttpServletRequest request) {
        Param existing = paramService.getById(id);
        if (existing == null) {
            throw new RuntimeException("参数不存在");
        }

        String oldName = existing.getName();
        String oldUnit = existing.getUnit();
        Boolean oldEnabled = existing.getEnabled();
        Set<Long> oldAssignedIds = new HashSet<>(parseAssignedCategoryIds(existing.getAssignedCategoryIds()));

        existing.setName(param.getName());
        existing.setUnit(param.getUnit());
        if (param.getEnabled() != null) {
            existing.setEnabled(param.getEnabled());
        }
        if (param.getAssignedCategoryIds() != null) {
            existing.setAssignedCategoryIds(param.getAssignedCategoryIds());
        }
        if (Boolean.FALSE.equals(existing.getEnabled())) {
            existing.setAssignedCategoryIds("[]");
        }

        Set<Long> newAssignedIds = new HashSet<>(parseAssignedCategoryIds(existing.getAssignedCategoryIds()));
        List<Long> addedCategoryIds = newAssignedIds.stream()
                .filter(categoryId -> !oldAssignedIds.contains(categoryId))
                .toList();
        List<Long> removedCategoryIds = oldAssignedIds.stream()
                .filter(categoryId -> !newAssignedIds.contains(categoryId))
                .toList();
        boolean assignmentChanged = !addedCategoryIds.isEmpty() || !removedCategoryIds.isEmpty();
        boolean nameChanged = !Objects.equals(oldName, existing.getName());
        boolean unitChanged = !Objects.equals(oldUnit, existing.getUnit());
        boolean enabledChanged = !Objects.equals(oldEnabled, existing.getEnabled());

        paramService.updateById(existing);
        syncCategorySpecs();

        try {
            List<String> changes = new ArrayList<>();
            if (nameChanged) {
                changes.add("名称「" + oldName + "」→「" + existing.getName() + "」");
            }
            if (unitChanged) {
                changes.add("单位「" + displayValue(oldUnit) + "」→「" + displayValue(existing.getUnit()) + "」");
            }
            if (enabledChanged) {
                changes.add("状态「" + enabledLabel(oldEnabled) + "」→「" + enabledLabel(existing.getEnabled()) + "」");
            }
            if (!addedCategoryIds.isEmpty()) {
                changes.add("分配类目：" + formatCategoryNames(addedCategoryIds));
            }
            if (!removedCategoryIds.isEmpty()) {
                changes.add("取消分配类目：" + formatCategoryNames(removedCategoryIds));
            }

            boolean onlyAssignmentChanged = assignmentChanged && !nameChanged && !unitChanged && !enabledChanged;
            String action = onlyAssignmentChanged ? "分配产品参数" : "更新产品参数";
            String detail = changes.isEmpty()
                    ? "保存参数「" + existing.getName() + "」，内容无变化"
                    : "保存参数「" + existing.getName() + "」：" + String.join("；", changes);
            auditLogService.record(getUsername(request), action, "param:" + id, detail,
                    clientIp(request), request.getHeader("User-Agent"), true);
        } catch (Exception ignored) {}
        return existing;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, HttpServletRequest request) {
        Param param = paramService.getById(id);
        try {
            auditLogService.record(getUsername(request), "删除产品参数", "param:" + id,
                    "删除参数「" + (param != null ? param.getName() : String.valueOf(id)) + "」",
                    clientIp(request), request.getHeader("User-Agent"), true);
        } catch (Exception ignored) {}
        paramService.removeById(id);
        syncCategorySpecs();
    }

    @PutMapping("/{id}/toggle")
    public Param toggle(@PathVariable Long id, HttpServletRequest request) {
        Param param = paramService.getById(id);
        if (param == null) {
            throw new RuntimeException("参数不存在");
        }
        boolean willEnable = !Boolean.TRUE.equals(param.getEnabled());
        param.setEnabled(willEnable);
        if (!param.getEnabled()) {
            param.setAssignedCategoryIds("[]");
        }
        paramService.updateById(param);
        syncCategorySpecs();

        try {
            auditLogService.record(getUsername(request), willEnable ? "启用产品参数" : "停用产品参数", "param:" + id,
                    (willEnable ? "启用" : "停用") + "参数「" + param.getName() + "」",
                    clientIp(request), request.getHeader("User-Agent"), true);
        } catch (Exception ignored) {}
        return param;
    }

    @GetMapping("/categories/list")
    public List<Category> getAssignedCategories(@RequestParam Long paramId) {
        Param param = paramService.getById(paramId);
        if (param == null || param.getAssignedCategoryIds() == null || param.getAssignedCategoryIds().isBlank()) {
            return Collections.emptyList();
        }

        List<Category> result = new ArrayList<>();
        for (Long categoryId : parseAssignedCategoryIds(param.getAssignedCategoryIds())) {
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                result.add(category);
            }
        }
        return result;
    }

    private String getUsername(HttpServletRequest request) {
        try {
            org.springframework.security.core.Authentication auth =
                    org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                return auth.getName();
            }
        } catch (Exception ignored) {}
        return null;
    }

    private String clientIp(HttpServletRequest request) {
        return AuditLogInterceptor.clientIp(request);
    }

    private String displayValue(String value) {
        return value == null || value.isBlank() ? "无" : value;
    }

    private String enabledLabel(Boolean enabled) {
        return Boolean.TRUE.equals(enabled) ? "启用" : "停用";
    }

    private String formatCategoryNames(List<Long> categoryIds) {
        List<String> names = categoryIds.stream()
                .map(id -> {
                    Category category = categoryService.getById(id);
                    return category != null ? category.getNameEn() : "类目#" + id;
                })
                .toList();
        return String.join("、", names);
    }

    private List<Long> parseAssignedCategoryIds(String value) {
        List<Long> ids = new ArrayList<>();
        if (value == null || value.isBlank()) {
            return ids;
        }
        try {
            JsonNode node = objectMapper.readTree(value);
            if (node != null && node.isArray()) {
                node.forEach(item -> {
                    if (item.canConvertToLong()) {
                        ids.add(item.asLong());
                    }
                });
                return ids;
            }
        } catch (Exception ignored) {
            // Legacy values were stored as a comma-separated list.
        }

        for (String part : value.split(",")) {
            try {
                if (!part.trim().isEmpty()) {
                    ids.add(Long.parseLong(part.trim()));
                }
            } catch (NumberFormatException ignored) {
                // Ignore malformed legacy values.
            }
        }
        return ids;
    }

    private void syncCategorySpecs() {
        List<Param> enabledParams = paramService.listAll().stream()
                .filter(p -> Boolean.TRUE.equals(p.getEnabled()))
                .toList();

        for (Category category : categoryService.findByType("PRODUCT")) {
            List<Param> assignedParams = enabledParams.stream()
                    .filter(p -> parseAssignedCategoryIds(
                            p.getAssignedCategoryIds() == null ? "[]" : p.getAssignedCategoryIds())
                            .contains(category.getId()))
                    .toList();
            List<Param> orderedParams = preserveCategoryParamOrder(category.getSpecs(), assignedParams);
            try {
                category.setSpecs(objectMapper.writeValueAsString(orderedParams));
                categoryService.updateById(category);
            } catch (Exception ignored) {
                // Category updates should not make the parameter request fail.
            }
        }
    }

    private List<Param> preserveCategoryParamOrder(String currentSpecs, List<Param> assignedParams) {
        Map<Long, Param> assignedById = new LinkedHashMap<>();
        assignedParams.forEach(param -> assignedById.put(param.getId(), param));

        List<Param> ordered = new ArrayList<>();
        for (Long id : parseCategorySpecIds(currentSpecs)) {
            Param param = assignedById.remove(id);
            if (param != null) {
                ordered.add(param);
            }
        }
        ordered.addAll(assignedById.values());
        return ordered;
    }

    private List<Long> parseCategorySpecIds(String specs) {
        List<Long> ids = new ArrayList<>();
        if (specs == null || specs.isBlank()) return ids;
        try {
            JsonNode root = objectMapper.readTree(specs);
            if (root != null && root.isArray()) {
                root.forEach(item -> {
                    JsonNode id = item.get("id");
                    if (id != null && id.canConvertToLong()) {
                        ids.add(id.asLong());
                    }
                });
            }
        } catch (Exception ignored) {
            // Invalid legacy JSON is replaced by the current assignment order.
        }
        return ids;
    }
}
