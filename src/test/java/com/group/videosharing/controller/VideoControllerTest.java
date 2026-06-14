package com.group.videosharing.controller;

import com.group.videosharing.domain.VideoEntity;
import com.group.videosharing.dto.UserDto;
import com.group.videosharing.dto.VideoDto;
import com.group.videosharing.patterns.behavioral.command.CommandHistory;
import com.group.videosharing.patterns.creational.singleton.EventBus;
import com.group.videosharing.patterns.creational.singleton.SessionManager;
import com.group.videosharing.patterns.structural.proxy.InteractionServiceProxy;
import com.group.videosharing.patterns.structural.proxy.RealInteractionService;
import com.group.videosharing.repository.VideoRepository;
import com.group.videosharing.service.SubscriptionService;
import com.group.videosharing.service.VideoMapper;
import com.group.videosharing.service.VideoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VideoControllerTest {

    private VideoController controller;
    private FakeVideoRepository videoRepository;
    private SubscriptionService subscriptionService;
    private SessionManager sessionManager;

    @BeforeEach
    void setUp() {
        videoRepository = new FakeVideoRepository();
        VideoService videoService = new VideoService(videoRepository.proxy(), new VideoMapper());
        subscriptionService = new SubscriptionService();
        RealInteractionService realInteractionService = new RealInteractionService(videoService, subscriptionService);
        InteractionServiceProxy interactionService = new InteractionServiceProxy(realInteractionService);
        controller = new VideoController(
                videoService,
                interactionService,
                subscriptionService,
                new CommandHistory());
        sessionManager = SessionManager.getInstance();
        sessionManager.logout();
        EventBus.getInstance().clearAllHandlers();
    }

    @AfterEach
    void tearDown() {
        sessionManager.logout();
        EventBus.getInstance().clearAllHandlers();
    }

    @Test
    void getVideoReturnsOkWhenFound() {
        videoRepository.put(video("video-1", 0, 0, 0));

        ResponseEntity<?> response = controller.getVideo("video-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("video-1", ((VideoDto) response.getBody()).getId());
    }

    @Test
    void getVideoReturnsNotFoundWhenMissing() {
        ResponseEntity<?> response = controller.getVideo("missing");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Video not found: missing", response.getBody());
    }

    @Test
    void viewVideoIncrementsViewCount() {
        videoRepository.put(video("video-1", 5, 0, 0));

        ResponseEntity<?> response = controller.viewVideo("video-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(6L, ((VideoDto) response.getBody()).getViewCount());
    }

    @Test
    void likeVideoReturnsUnauthorizedWhenLoggedOut() {
        videoRepository.put(video("video-1", 0, 0, 0));

        ResponseEntity<?> response = controller.likeVideo("video-1");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(0L, videoRepository.get("video-1").getLikeCount());
    }

    @Test
    void likeVideoReturnsOkAndIncrementsLikeWhenLoggedIn() {
        videoRepository.put(video("video-1", 0, 2, 0));
        login("viewer-1");

        ResponseEntity<?> response = controller.likeVideo("video-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3L, ((VideoDto) response.getBody()).getLikeCount());
    }

    @Test
    void likeVideoReturnsNotFoundWhenVideoDoesNotExist() {
        login("viewer-1");

        ResponseEntity<?> response = controller.likeVideo("missing");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Video not found: missing", response.getBody());
    }

    @Test
    void dislikeVideoReturnsOkAndIncrementsDislikeWhenLoggedIn() {
        videoRepository.put(video("video-1", 0, 0, 4));
        login("viewer-1");

        ResponseEntity<?> response = controller.dislikeVideo("video-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5L, videoRepository.get("video-1").getDislikeCount());
    }

    @Test
    void subscribeAndUnsubscribeChannelUpdateSubscriberCount() {
        login("viewer-1");

        ResponseEntity<?> subscribeResponse = controller.subscribeChannel("channel-1");
        ResponseEntity<?> unsubscribeResponse = controller.unsubscribeChannel("channel-1");

        assertEquals(HttpStatus.OK, subscribeResponse.getStatusCode());
        assertEquals(1L, ((Map<?, ?>) subscribeResponse.getBody()).get("subscriberCount"));
        assertEquals(HttpStatus.OK, unsubscribeResponse.getStatusCode());
        assertEquals(0L, ((Map<?, ?>) unsubscribeResponse.getBody()).get("subscriberCount"));
    }

    @Test
    void undoAfterLikeDecrementsLikeCount() {
        videoRepository.put(video("video-1", 0, 1, 0));
        login("viewer-1");

        controller.likeVideo("video-1");
        ResponseEntity<?> response = controller.undoInteraction();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, videoRepository.get("video-1").getLikeCount());
    }

    @Test
    void undoAfterSubscribeUnsubscribesViewer() {
        login("viewer-1");

        controller.subscribeChannel("channel-1");
        ResponseEntity<?> response = controller.undoInteraction();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0L, subscriptionService.getSubscriberCount("channel-1"));
    }

    @Test
    void playerStateTransitionsThroughCoreStates() {
        ResponseEntity<?> firstPlay = controller.playVideo("video-1");
        ResponseEntity<?> secondPlay = controller.playVideo("video-1");
        ResponseEntity<?> pause = controller.pauseVideo("video-1");
        ResponseEntity<?> end = controller.endVideo("video-1");

        assertEquals("LoadingState", ((Map<?, ?>) firstPlay.getBody()).get("state"));
        assertEquals("PlayingState", ((Map<?, ?>) secondPlay.getBody()).get("state"));
        assertEquals("PausedState", ((Map<?, ?>) pause.getBody()).get("state"));
        assertEquals("EndedState", ((Map<?, ?>) end.getBody()).get("state"));
    }

    private void login(String userId) {
        UserDto user = new UserDto();
        user.setId(userId);
        user.setChannelId(userId);
        sessionManager.login(user);
    }

    private VideoEntity video(String id, long viewCount, long likeCount, long dislikeCount) {
        VideoEntity video = new VideoEntity.Builder()
                .setTitle("Design Patterns")
                .setDescription("Mini YouTube demo")
                .setVisibility("public")
                .setUploaderId("channel-1")
                .setDurationSeconds(180)
                .setCategory("Education")
                .build();
        ReflectionTestUtils.setField(video, "id", id);
        ReflectionTestUtils.setField(video, "viewCount", viewCount);
        ReflectionTestUtils.setField(video, "likeCount", likeCount);
        ReflectionTestUtils.setField(video, "dislikeCount", dislikeCount);
        ReflectionTestUtils.setField(video, "uploadedAt", LocalDateTime.of(2026, 6, 12, 10, 30));
        return video;
    }

    private static class FakeVideoRepository {
        private final Map<String, VideoEntity> videos = new HashMap<>();

        private void put(VideoEntity video) {
            videos.put(video.getId(), video);
        }

        private VideoEntity get(String id) {
            return videos.get(id);
        }

        private VideoRepository proxy() {
            return (VideoRepository) Proxy.newProxyInstance(
                    VideoRepository.class.getClassLoader(),
                    new Class<?>[]{VideoRepository.class},
                    (target, method, args) -> {
                        if (method.getName().equals("findById")) {
                            return Optional.ofNullable(videos.get((String) args[0]));
                        }
                        if (method.getName().equals("save")) {
                            VideoEntity video = (VideoEntity) args[0];
                            videos.put(video.getId(), video);
                            return video;
                        }
                        if (method.getName().equals("toString")) {
                            return "FakeVideoRepository";
                        }
                        throw new UnsupportedOperationException("Unsupported method: " + method.getName());
                    });
        }
    }
}
