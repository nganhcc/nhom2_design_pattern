package com.group.videosharing.controller;

import com.group.videosharing.dto.UserDto;
import com.group.videosharing.patterns.creational.singleton.SessionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthControllerTest {

    private AuthController authController;
    private SessionManager sessionManager;

    @BeforeEach
    void setUp() {
        authController = new AuthController();
        sessionManager = SessionManager.getInstance();
        sessionManager.logout();
    }

    @AfterEach
    void tearDown() {
        sessionManager.logout();
    }

    @Test
    void loginStoresUserInSession() {
        ResponseEntity<?> response = authController.login("user-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(sessionManager.isLoggedIn());
        assertEquals("user-1", sessionManager.getCurrentUserId());
        assertEquals("user-1", ((Map<?, ?>) response.getBody()).get("userId"));
    }

    @Test
    void loginRejectsBlankUserId() {
        ResponseEntity<?> response = authController.login(" ");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("userId must not be blank", response.getBody());
    }

    @Test
    void logoutClearsSession() {
        authController.login("user-1");

        ResponseEntity<?> response = authController.logout();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(sessionManager.isLoggedIn());
    }

    @Test
    void meReturnsUnauthorizedWhenLoggedOut() {
        ResponseEntity<?> response = authController.me();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void meReturnsCurrentUserWhenLoggedIn() {
        authController.login("user-1");

        ResponseEntity<?> response = authController.me();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserDto body = (UserDto) response.getBody();
        assertEquals("user-1", body.getId());
    }
}
