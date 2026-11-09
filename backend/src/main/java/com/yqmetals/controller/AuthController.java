package com.yqmetals.controller;

import com.yqmetals.entity.User;
import com.yqmetals.security.AuditLogInterceptor;
import com.yqmetals.security.JwtUtils;
import com.yqmetals.service.AuditLogService;
import com.yqmetals.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public AuthController(UserService userService, JwtUtils jwtUtils, PasswordEncoder passwordEncoder,
                          AuditLogService auditLogService) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req, HttpServletRequest request) {
        User user = userService.findByUsername(req.username())
                .filter(u -> u.getActive() && !u.getDeleted())
                .orElse(null);
        if (user == null) {
            recordLogin(req.username(), "登录失败：用户名不存在或已停用", request, false);
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
        boolean matches = passwordEncoder.matches(req.password(), user.getPassword());
        if (!matches) {
            recordLogin(req.username(), "登录失败：密码错误", request, false);
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
        String token = jwtUtils.generateToken(user.getUsername(), user.getRole().name());
        recordLogin(user.getUsername(), "登录成功", request, true);
        return ResponseEntity.ok(Map.of(
                "token", token,
                "user", Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "displayName", user.getDisplayName(),
                        "role", user.getRole().name()
                )
        ));
    }

    private void recordLogin(String username, String detail, HttpServletRequest request, boolean success) {
        try {
            auditLogService.record(username, "登录", "admin-login", detail,
                    AuditLogInterceptor.clientIp(request), request.getHeader("User-Agent"), success);
        } catch (Exception ignored) {
            // Never block login because of audit logging.
        }
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
}
