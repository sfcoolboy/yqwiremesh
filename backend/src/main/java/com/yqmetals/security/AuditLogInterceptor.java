package com.yqmetals.security;

import com.yqmetals.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuditLogInterceptor implements HandlerInterceptor {

    private final AuditLogService auditLogService;

    public AuditLogInterceptor(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        if (!uri.startsWith("/api/") || !isWriteMethod(method)) {
            return;
        }
        if (uri.startsWith("/api/auth/") || uri.startsWith("/api/inquiries/public/")) {
            return;
        }
        if (uri.startsWith("/api/inquiries/") && uri.endsWith("/assign")) {
            // Assignment is recorded explicitly in InquiryController so that
            // first assignment and re-assignment can be distinguished.
            return;
        }
        if ("/api/params".equals(uri) || uri.startsWith("/api/params/")) {
            // Parameter operations are recorded explicitly in ParamController.
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getName())) ? auth.getName() : null;

        String action = resolveAction(method, uri);
        boolean success = response.getStatus() < 400 && ex == null;
        String detail = success ? action : action + "（失败）";

        try {
            auditLogService.record(username, action, uri, detail,
                    clientIp(request), request.getHeader("User-Agent"), success);
        } catch (Exception ignored) {
            // Logging must never break the business request.
        }
    }

    private boolean isWriteMethod(String method) {
        return "POST".equals(method) || "PUT".equals(method)
                || "DELETE".equals(method) || "PATCH".equals(method);
    }

    private String resolveAction(String method, String uri) {
        String path = uri.replaceFirst("^/api/", "");
        if (path.startsWith("admin/")) {
            path = path.substring("admin/".length());
        }
        String resource = path.isEmpty() ? "" : path.split("/")[0];

        if ("users".equals(resource) && uri.endsWith("/password")) {
            return "重置用户密码";
        }
        if ("uploads".equals(resource)) {
            return "上传图片";
        }

        String label = switch (resource) {
            case "users" -> "用户";
            case "products" -> "产品";
            case "categories" -> "类目";
            case "news" -> "新闻";
            case "factory-images" -> "工厂图片";
            case "settings" -> "系统设置";
            case "inquiries" -> "询盘";
            default -> resource;
        };
        return switch (method) {
            case "POST" -> "创建" + label;
            case "PUT", "PATCH" -> "更新" + label;
            case "DELETE" -> "删除" + label;
            default -> method + " " + label;
        };
    }

    public static String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp;
        }
        return request.getRemoteAddr();
    }
}
