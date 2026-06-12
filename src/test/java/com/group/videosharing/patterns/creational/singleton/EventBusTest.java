package com.group.videosharing.patterns.creational.singleton;

import com.group.videosharing.domain.VideoEntity;
import com.group.videosharing.patterns.behavioral.observer.IEventHandler;
import com.group.videosharing.patterns.behavioral.observer.LikeChangedEvent;
import com.group.videosharing.patterns.behavioral.observer.VideoUploadedEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EventBusTest {

    private EventBus eventBus;

    @BeforeEach
    void setUp() {
        eventBus = EventBus.getInstance();
        eventBus.clearAllHandlers();
    }

    @AfterEach
    void tearDown() {
        eventBus.clearAllHandlers();
    }

    @Test
    void subscribeThenPublishDeliversEventToHandler() {
        VideoEntity video = new VideoEntity.Builder()
                .setTitle("Design Pattern Demo")
                .setVisibility("public")
                .setUploaderId("user-1")
                .build();
        List<String> receivedTitles = new ArrayList<>();

        eventBus.subscribe(VideoUploadedEvent.class, event -> receivedTitles.add(event.video().getTitle()));
        eventBus.publish(new VideoUploadedEvent(video));

        assertEquals(List.of("Design Pattern Demo"), receivedTitles);
    }

    @Test
    void publishDeliversEventToMultipleHandlers() {
        AtomicInteger callCount = new AtomicInteger();

        eventBus.subscribe(LikeChangedEvent.class, event -> callCount.incrementAndGet());
        eventBus.subscribe(LikeChangedEvent.class, event -> callCount.addAndGet((int) event.newCount()));
        eventBus.publish(new LikeChangedEvent("video-1", 4));

        assertEquals(5, callCount.get());
    }

    @Test
    void publishWithoutSubscribersDoesNotFail() {
        assertDoesNotThrow(() -> eventBus.publish(new LikeChangedEvent("video-1", 10)));
    }

    @Test
    void unsubscribeRemovesHandler() {
        AtomicInteger callCount = new AtomicInteger();
        IEventHandler<LikeChangedEvent> handler = event -> callCount.incrementAndGet();

        eventBus.subscribe(LikeChangedEvent.class, handler);
        eventBus.unsubscribe(LikeChangedEvent.class, handler);
        eventBus.publish(new LikeChangedEvent("video-1", 1));

        assertEquals(0, callCount.get());
    }

    @Test
    void clearAllHandlersRemovesExistingSubscriptions() {
        AtomicInteger callCount = new AtomicInteger();

        eventBus.subscribe(LikeChangedEvent.class, event -> callCount.incrementAndGet());
        eventBus.clearAllHandlers();
        eventBus.publish(new LikeChangedEvent("video-1", 1));

        assertEquals(0, callCount.get());
    }

    @Test
    void publishNullThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> eventBus.publish(null));
    }
}
