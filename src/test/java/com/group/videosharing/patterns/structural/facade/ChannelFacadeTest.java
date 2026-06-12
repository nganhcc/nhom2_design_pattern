package com.group.videosharing.patterns.structural.facade;

import com.group.videosharing.dto.ChannelPageViewModel;
import com.group.videosharing.dto.UserDto;
import com.group.videosharing.dto.VideoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChannelFacadeTest {

    private FakeVideoService videoService;
    private FakeUserService userService;
    private FakeSubscriptionService subscriptionService;
    private ChannelFacade channelFacade;

    @BeforeEach
    void setUp() {
        videoService = new FakeVideoService();
        userService = new FakeUserService();
        subscriptionService = new FakeSubscriptionService();
        channelFacade = new ChannelFacade(videoService, userService, subscriptionService);
    }

    @Test
    void getChannelPageMergesUserVideosSubscriptionAndSubscriberCount() {
        userService.user = user("channel-1");
        videoService.videos = List.of(video("video-1"));
        subscriptionService.subscribed = true;
        subscriptionService.subscriberCount = 8;

        ChannelPageViewModel result = channelFacade.getChannelPage("channel-1", "viewer-1");

        assertSame(userService.user, result.getChannelInfo());
        assertEquals(videoService.videos, result.getVideos());
        assertTrue(result.isSubscribed());
        assertEquals(8, result.getSubscriberCount());
        assertEquals("channel-1", userService.lastUserId);
        assertEquals("channel-1", videoService.lastChannelId);
        assertEquals("viewer-1", subscriptionService.lastViewerId);
        assertEquals("channel-1", subscriptionService.lastIsSubscribedChannelId);
        assertEquals("channel-1", subscriptionService.lastCountChannelId);
    }

    @Test
    void nullViewerIdReturnsNotSubscribed() {
        userService.user = user("channel-1");
        videoService.videos = List.of(video("video-1"));
        subscriptionService.subscribed = false;

        ChannelPageViewModel result = channelFacade.getChannelPage("channel-1", null);

        assertFalse(result.isSubscribed());
        assertEquals(null, subscriptionService.lastViewerId);
    }

    @Test
    void blankViewerIdReturnsNotSubscribed() {
        userService.user = user("channel-1");
        videoService.videos = List.of(video("video-1"));
        subscriptionService.subscribed = false;

        ChannelPageViewModel result = channelFacade.getChannelPage("channel-1", " ");

        assertFalse(result.isSubscribed());
        assertEquals(" ", subscriptionService.lastViewerId);
    }

    @Test
    void blankChannelIdThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> channelFacade.getChannelPage(" ", "viewer-1"));
    }

    @Test
    void serviceNoSuchElementExceptionIsUnwrappedFromAsyncTask() {
        userService.error = new NoSuchElementException("User not found: channel-404");

        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> channelFacade.getChannelPage("channel-404", "viewer-1"));
        assertEquals("User not found: channel-404", ex.getMessage());
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
        private String lastChannelId;

        @Override
        public List<VideoDto> getVideosByChannel(String channelId) {
            lastChannelId = channelId;
            return videos;
        }

        @Override
        public VideoDto getVideoById(String videoId) {
            throw new UnsupportedOperationException("Not needed for ChannelFacadeTest");
        }
    }

    private static class FakeUserService implements IUserService {
        private UserDto user;
        private RuntimeException error;
        private String lastUserId;

        @Override
        public UserDto getUserById(String userId) {
            lastUserId = userId;
            if (error != null) {
                throw error;
            }
            return user;
        }
    }

    private static class FakeSubscriptionService implements ISubscriptionService {
        private boolean subscribed;
        private long subscriberCount;
        private String lastViewerId;
        private String lastIsSubscribedChannelId;
        private String lastCountChannelId;

        @Override
        public boolean isSubscribed(String viewerId, String channelId) {
            lastViewerId = viewerId;
            lastIsSubscribedChannelId = channelId;
            return subscribed;
        }

        @Override
        public long getSubscriberCount(String channelId) {
            lastCountChannelId = channelId;
            return subscriberCount;
        }
    }
}
