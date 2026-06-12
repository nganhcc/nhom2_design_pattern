package com.group.videosharing.patterns.creational.singleton;

import com.group.videosharing.dto.UserDto;

/**
 * Singleton — Pattern 13
 * Quản lý auth state toàn app.
 */
public class SessionManager {

    private static volatile SessionManager instance;
    private volatile UserDto currentUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) instance = new SessionManager();
            }
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public UserDto getCurrentUser() {
        return currentUser;
    }

    public String getCurrentUserId() {
        return requireLogin().getId();
    }

    public void login(UserDto user) {
        if (user == null) {
            throw new IllegalArgumentException("user must not be null");
        }
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public UserDto requireLogin() {
        if (!isLoggedIn()) {
            throw new IllegalStateException("Unauthorized - chưa đăng nhập");
        }
        return currentUser;
    }
}
