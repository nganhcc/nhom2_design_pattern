package com.group.videosharing.patterns.behavioral.cor;

import com.group.videosharing.dto.CommentRequest;
import com.group.videosharing.dto.UserDto;
import com.group.videosharing.dto.ValidationResult;
import com.group.videosharing.patterns.creational.singleton.SessionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentHandlerTest {

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
    void chainStopsAtAuthenticationFailure() {
        CommentHandler chain = new AuthCheckHandler();
        chain.setNext(new EmptyContentHandler());

        ValidationResult result = chain.handle(request("video-1", ""));

        assertEquals(false, result.isValid());
        assertEquals("Chưa đăng nhập", result.getError());
    }

    @Test
    void spamFilterRejectsBlockedKeyword() {
        sessionManager.login(user("user-1"));
        CommentHandler chain = new AuthCheckHandler();
        chain.setNext(new VideoIdRequiredHandler())
                .setNext(new EmptyContentHandler())
                .setNext(new MaxLengthHandler())
                .setNext(new SpamFilterHandler());

        ValidationResult result = chain.handle(request("video-1", "This is a scam"));

        assertEquals(false, result.isValid());
        assertEquals("Nội dung spam", result.getError());
    }

    private CommentRequest request(String videoId, String text) {
        CommentRequest request = new CommentRequest();
        request.setVideoId(videoId);
        request.setText(text);
        return request;
    }

    private UserDto user(String id) {
        UserDto user = new UserDto();
        user.setId(id);
        user.setChannelId(id);
        return user;
    }
}
