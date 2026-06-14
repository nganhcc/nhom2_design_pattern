package com.group.videosharing.patterns.structural.proxy;

import com.group.videosharing.dto.UserDto;
import com.group.videosharing.patterns.creational.singleton.SessionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InteractionServiceProxyTest {

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
    void proxyRejectsInteractionWhenLoggedOut() {
        InteractionServiceProxy proxy = new InteractionServiceProxy(new FakeRealInteractionService());

        assertThrows(IllegalStateException.class, () -> proxy.like("video-1"));
    }

    @Test
    void proxyDelegatesInteractionWhenLoggedIn() {
        FakeRealInteractionService real = new FakeRealInteractionService();
        InteractionServiceProxy proxy = new InteractionServiceProxy(real);
        sessionManager.login(user("user-1"));

        proxy.like("video-1");

        assertEquals(1, real.likeCount);
    }

    private UserDto user(String id) {
        UserDto user = new UserDto();
        user.setId(id);
        user.setChannelId(id);
        return user;
    }

    private static class FakeRealInteractionService extends RealInteractionService {
        private int likeCount;

        private FakeRealInteractionService() {
            super(null, null);
        }

        @Override public void like(String videoId) { likeCount++; }
        @Override public void unlike(String videoId) {}
        @Override public void dislike(String videoId) {}
        @Override public void undislike(String videoId) {}
        @Override public void subscribe(String channelId) {}
        @Override public void unsubscribe(String channelId) {}
        @Override public void addComment(String videoId, String text) {}
        @Override public void deleteComment(String commentId) {}
    }
}
