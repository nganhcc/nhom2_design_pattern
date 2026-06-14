package com.group.videosharing.controller;

import com.group.videosharing.dto.UserDto;
import com.group.videosharing.patterns.creational.singleton.SessionManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final SessionManager sessionManager = SessionManager.getInstance();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String userId) {
        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().body("userId must not be blank");
        }

        UserDto user = new UserDto();
        user.setId(userId.trim());
        user.setChannelId(userId.trim());
        user.setUsername(userId.trim());
        sessionManager.login(user);

        return ResponseEntity.ok(Map.of(
                "loggedIn", true,
                "userId", user.getId()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        sessionManager.logout();
        return ResponseEntity.ok(Map.of("loggedIn", false));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        if (!sessionManager.isLoggedIn()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized - chưa đăng nhập");
        }

        return ResponseEntity.ok(sessionManager.getCurrentUser());
    }
}
