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

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {

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
        User user = userService.register(req.username(), req.password(), req.displayName(), req.role());
        return ResponseEntity.ok(Map.of("id", user.getId(), "username", user.getUsername()));
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User u) {
        u.setId(id);
        userService.updateById(u);
        return u;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.removeById(id);
    }

    public record CreateUserReq(
            @NotBlank String username,
            @NotBlank String password,
            @NotBlank String displayName,
            @NotBlank String role
    ) {}
}
