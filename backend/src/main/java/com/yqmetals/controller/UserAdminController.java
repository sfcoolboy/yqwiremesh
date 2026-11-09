package com.yqmetals.controller;

import com.yqmetals.entity.User;
import com.yqmetals.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final UserService userService;

    public UserAdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> list() {
        return userService.findAllActive();
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateUserReq req) {
        String role = req.role().trim().toUpperCase();
        String workEmail = normalize(req.workEmail());
        String validationError = validateSalesEmail(role, workEmail);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(Map.of("error", validationError));
        }
        User user = userService.register(req.username(), req.password(), req.displayName(),
                normalize(req.employeeNumber()), workEmail, role, req.active());
        return ResponseEntity.ok(Map.of("id", user.getId(), "username", user.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody User u) {
        User existing = userService.getById(id);
        if (existing == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "用户不存在"));
        }

        String effectiveRole = u.getRole() != null ? u.getRole().name() : existing.getRole().name();
        String effectiveWorkEmail = u.getWorkEmail() != null ? normalize(u.getWorkEmail()) : existing.getWorkEmail();
        String validationError = validateSalesEmail(effectiveRole, effectiveWorkEmail);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(Map.of("error", validationError));
        }

        u.setId(id);
        if (u.getWorkEmail() != null) {
            u.setWorkEmail(normalize(u.getWorkEmail()));
        }
        userService.updateUser(u);
        return ResponseEntity.ok(userService.getById(id));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<?> resetPassword(@PathVariable("id") Long id, @RequestBody Map<String, String> body) {
        String password = body.get("password");
        if (password == null || password.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password must be at least 6 characters"));
        }
        userService.resetPassword(id, password);
        return ResponseEntity.ok(Map.of("message", "Password reset"));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.deletePhysically(id);
    }

    private String validateSalesEmail(String role, String workEmail) {
        if (!"ADMIN".equals(role) && !"SALES".equals(role)) {
            return "用户角色无效";
        }
        if ("SALES".equals(role) && (workEmail == null || workEmail.isBlank())) {
            return "业务员必须填写工作邮箱";
        }
        if (workEmail != null && !workEmail.isBlank() && !EMAIL_PATTERN.matcher(workEmail).matches()) {
            return "工作邮箱格式不正确";
        }
        return null;
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    public record CreateUserReq(
            @NotBlank String username,
            @NotBlank String password,
            @NotBlank String displayName,
            String employeeNumber,
            String workEmail,
            @NotBlank String role,
            Boolean active
    ) {}
}
