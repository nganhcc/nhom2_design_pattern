package com.group.videosharing.controller;

import com.group.videosharing.dto.ChannelPageViewModel;
import com.group.videosharing.dto.UserDto;
import com.group.videosharing.dto.VideoDto;
import com.group.videosharing.patterns.structural.facade.ChannelFacade;
import com.group.videosharing.patterns.structural.facade.ISubscriptionService;
import com.group.videosharing.patterns.structural.facade.IUserService;
import com.group.videosharing.patterns.structural.facade.IVideoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ChannelControllerTest {

    private FakeUserService userService;
    private FakeVideoService videoService;
    private FakeSubscriptionService subscriptionService;
    private ChannelController channelController;

    @BeforeEach
    void setUp() {
        userService = new FakeUserService();
        videoService = new FakeVideoService();
        subscriptionService = new FakeSubscriptionService();
        channelController = new ChannelController(new ChannelFacade(videoService, userService, subscriptionService));
    }

    @Test
    void getChannelPageReturnsOkWithViewModel() {
        userService.user = user("channel-1");
        videoService.videos = List.of(video("video-1"));
        subscriptionService.subscriberCount = 5;

        ResponseEntity<?> response = channelController.getChannelPage("channel-1", "viewer-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ChannelPageViewModel body = (ChannelPageViewModel) response.getBody();
        assertSame(userService.user, body.getChannelInfo());
        assertEquals(videoService.videos, body.getVideos());
        assertEquals(5, body.getSubscriberCount());
    }

    @Test
    void getChannelPageReturnsBadRequestForInvalidChannelId() {
        ResponseEntity<?> response = channelController.getChannelPage(" ", "viewer-1");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("channelId must not be blank", response.getBody());
    }

    @Test
    void getChannelPageReturnsNotFoundWhenChannelDoesNotExist() {
        userService.error = new NoSuchElementException("User not found: missing");

        ResponseEntity<?> response = channelController.getChannelPage("missing", "viewer-1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found: missing", response.getBody());
    }

    private UserDto user(String id) {
        UserDto user = new UserDto();
        user.setId(id);
        user.setUsername("member4");
        user.setEmail("member4@example.com");
        user.setChannelId(id);
        return user;
    }

    private VideoDto video(String id) {
        VideoDto video = new VideoDto();
        video.setId(id);
        video.setTitle("Design Patterns");
        video.setChannelId("channel-1");
        video.setUploaderId("channel-1");
        return video;
    }

    private static class FakeVideoService implements IVideoService {
        private List<VideoDto> videos = List.of();

        @Override
        public List<VideoDto> getVideosByChannel(String channelId) {
            return videos;
        }

        @Override
        public VideoDto getVideoById(String videoId) {
            throw new UnsupportedOperationException("Not needed for ChannelControllerTest");
        }
    }

    private static class FakeUserService implements IUserService {
        private UserDto user;
        private RuntimeException error;

        @Override
        public UserDto getUserById(String userId) {
            if (error != null) {
                throw error;
            }
            return user;
        }
    }

    private static class FakeSubscriptionService implements ISubscriptionService {
        private long subscriberCount;

        @Override
        public boolean isSubscribed(String viewerId, String channelId) {
            return false;
        }

        @Override
        public long getSubscriberCount(String channelId) {
            return subscriberCount;
        }
    }
}
