package com.yqmetals.controller;

import com.yqmetals.entity.User;
import com.yqmetals.security.JwtUtils;
import com.yqmetals.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    public AuthController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        User user = userService.findByUsername(req.username())
                .filter(u -> u.getActive() && !u.getDeleted())
                .orElse(null);
        if (user == null || !req.password().equals(user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
        String token = jwtUtils.generateToken(user.getUsername(), user.getRole().name());
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

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
}
