package com.group.videosharing.controller;

import com.group.videosharing.patterns.behavioral.command.CommandHistory;
import com.group.videosharing.patterns.structural.proxy.IInteractionService;
import com.group.videosharing.service.SubscriptionService;
import com.group.videosharing.service.VideoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VideoControllerTest {

    private VideoController controller;
    private FakeInteractionService fakeInteractionService;
    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        subscriptionService = new SubscriptionService();
        fakeInteractionService = new FakeInteractionService(subscriptionService);
        controller = new VideoController(
                new VideoService(null, null),
                fakeInteractionService,
                subscriptionService,
                new CommandHistory());
    }

    @Test
    void playVideoTransitionsToLoadingThenPlaying() {
        ResponseEntity<?> first = controller.playVideo("video-1");
        assertEquals(200, first.getStatusCode().value());
        assertEquals("LoadingState", ((java.util.Map<?, ?>) first.getBody()).get("state"));

        ResponseEntity<?> second = controller.playVideo("video-1");
        assertEquals(200, second.getStatusCode().value());
        assertEquals("PlayingState", ((java.util.Map<?, ?>) second.getBody()).get("state"));
    }

    @Test
    void subscribeChannelReturnsSubscriberCount() {
        ResponseEntity<?> response = controller.subscribeChannel("channel-1");

        assertEquals(200, response.getStatusCode().value());
        assertTrue(((java.util.Map<?, ?>) response.getBody()).containsKey("subscriberCount"));
        assertEquals(1L, ((java.util.Map<?, ?>) response.getBody()).get("subscriberCount"));
    }

    private static class FakeInteractionService implements IInteractionService {
        private final SubscriptionService subscriptionService;

        public FakeInteractionService(SubscriptionService subscriptionService) {
            this.subscriptionService = subscriptionService;
        }

        @Override
        public void like(String videoId) {
            // no-op
        }

        @Override
        public void unlike(String videoId) {
            // no-op
        }

        @Override
        public void dislike(String videoId) {
            // no-op
        }

        @Override
        public void undislike(String videoId) {
            // no-op
        }

        @Override
        public void subscribe(String channelId) {
            subscriptionService.subscribe("viewer-1", channelId);
        }

        @Override
        public void unsubscribe(String channelId) {
            subscriptionService.unsubscribe("viewer-1", channelId);
        }

        @Override
        public void addComment(String videoId, String text) {
            // no-op
        }

        @Override
        public void deleteComment(String commentId) {
            // no-op
        }
    }
}
