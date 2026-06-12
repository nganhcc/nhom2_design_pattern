package com.group.videosharing.patterns.creational.singleton;

import com.group.videosharing.dto.UserDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SessionManagerTest {

    private SessionManager sessionManager;

    @BeforeEach
    void setUp() {
        sessionManager = SessionManager.getInstance();
        sessionManager.logout();
    }

    @AfterEach
    void tearDown() {
        sessionManager.logout();
    }

    @Test
    void startsWithoutLoggedInUser() {
        assertFalse(sessionManager.isLoggedIn());
    }

    @Test
    void loginStoresCurrentUser() {
        UserDto user = user("user-1");

        sessionManager.login(user);

        assertTrue(sessionManager.isLoggedIn());
        assertSame(user, sessionManager.getCurrentUser());
    }

    @Test
    void getCurrentUserIdReturnsLoggedInUserId() {
        sessionManager.login(user("user-1"));

        assertEquals("user-1", sessionManager.getCurrentUserId());
    }

    @Test
    void logoutClearsCurrentUser() {
        sessionManager.login(user("user-1"));

        sessionManager.logout();

        assertFalse(sessionManager.isLoggedIn());
        assertNull(sessionManager.getCurrentUser());
    }

    @Test
    void requireLoginThrowsWhenUserIsNotLoggedIn() {
        assertThrows(IllegalStateException.class, () -> sessionManager.requireLogin());
    }

    @Test
    void requireLoginReturnsCurrentUserWhenLoggedIn() {
        UserDto user = user("user-1");
        sessionManager.login(user);

        assertSame(user, sessionManager.requireLogin());
    }

    @Test
    void loginNullThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> sessionManager.login(null));
    }

    private UserDto user(String id) {
        UserDto user = new UserDto();
        user.setId(id);
        user.setUsername("member4");
        user.setEmail("member4@example.com");
        user.setChannelId("channel-1");
        return user;
    }
}
