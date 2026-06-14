package com.group.videosharing.patterns.structural.proxy;

import com.group.videosharing.dto.UserDto;
import com.group.videosharing.patterns.behavioral.observer.SubscriptionChangedEvent;
import com.group.videosharing.patterns.creational.singleton.EventBus;
import com.group.videosharing.patterns.creational.singleton.SessionManager;
import com.group.videosharing.service.SubscriptionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RealInteractionServiceTest {

    private EventBus eventBus;
    private SessionManager sessionManager;
    private SubscriptionService subscriptionService;
    private RealInteractionService realInteractionService;

    @BeforeEach
    void setUp() {
        eventBus = EventBus.getInstance();
        eventBus.clearAllHandlers();
        sessionManager = SessionManager.getInstance();
        sessionManager.logout();
        subscriptionService = new SubscriptionService();
        realInteractionService = new RealInteractionService(null, subscriptionService);
    }

    @AfterEach
    void tearDown() {
        eventBus.clearAllHandlers();
        sessionManager.logout();
    }

    @Test
    void subscribePublishesEventAndUpdatesSubscriberCount() {
        sessionManager.login(user("viewer-1"));
        List<SubscriptionChangedEvent> events = new ArrayList<>();
        eventBus.subscribe(SubscriptionChangedEvent.class, events::add);

        realInteractionService.subscribe("channel-1");

        assertEquals(1L, subscriptionService.getSubscriberCount("channel-1"));
        assertEquals("channel-1", events.getFirst().channelId());
        assertEquals(1, events.getFirst().delta());
    }

    @Test
    void unsubscribePublishesEventAndUpdatesSubscriberCount() {
        sessionManager.login(user("viewer-1"));
        subscriptionService.subscribe("viewer-1", "channel-1");
        List<SubscriptionChangedEvent> events = new ArrayList<>();
        eventBus.subscribe(SubscriptionChangedEvent.class, events::add);

        realInteractionService.unsubscribe("channel-1");

        assertEquals(0L, subscriptionService.getSubscriberCount("channel-1"));
        assertEquals("channel-1", events.getFirst().channelId());
        assertEquals(-1, events.getFirst().delta());
    }

    private UserDto user(String id) {
        UserDto user = new UserDto();
        user.setId(id);
        user.setChannelId(id);
        return user;
    }
}
