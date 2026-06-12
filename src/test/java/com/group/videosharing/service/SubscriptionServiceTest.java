package com.group.videosharing.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubscriptionServiceTest {

    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        subscriptionService = new SubscriptionService();
    }

    @Test
    void startsWithoutSubscriptions() {
        assertFalse(subscriptionService.isSubscribed("viewer-1", "channel-1"));
        assertEquals(0, subscriptionService.getSubscriberCount("channel-1"));
    }

    @Test
    void subscribeMarksViewerAsSubscribedAndIncrementsCount() {
        subscriptionService.subscribe("viewer-1", "channel-1");

        assertTrue(subscriptionService.isSubscribed("viewer-1", "channel-1"));
        assertEquals(1, subscriptionService.getSubscriberCount("channel-1"));
    }

    @Test
    void duplicateSubscribeDoesNotIncrementCountTwice() {
        subscriptionService.subscribe("viewer-1", "channel-1");
        subscriptionService.subscribe("viewer-1", "channel-1");

        assertEquals(1, subscriptionService.getSubscriberCount("channel-1"));
    }

    @Test
    void unsubscribeRemovesViewerAndDecrementsCount() {
        subscriptionService.subscribe("viewer-1", "channel-1");

        subscriptionService.unsubscribe("viewer-1", "channel-1");

        assertFalse(subscriptionService.isSubscribed("viewer-1", "channel-1"));
        assertEquals(0, subscriptionService.getSubscriberCount("channel-1"));
    }

    @Test
    void blankQueriesReturnFalseAndZero() {
        assertFalse(subscriptionService.isSubscribed("", "channel-1"));
        assertFalse(subscriptionService.isSubscribed("viewer-1", " "));
        assertEquals(0, subscriptionService.getSubscriberCount(" "));
    }
}
