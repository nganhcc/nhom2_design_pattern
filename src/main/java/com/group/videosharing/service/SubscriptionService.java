package com.group.videosharing.service;

import com.group.videosharing.patterns.structural.facade.ISubscriptionService;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SubscriptionService implements ISubscriptionService {

    private final ConcurrentHashMap<String, Set<String>> subscribersByChannel = new ConcurrentHashMap<>();

    @Override
    public boolean isSubscribed(String viewerId, String channelId) {
        if (isBlank(viewerId) || isBlank(channelId)) {
            return false;
        }
        return subscribersByChannel.getOrDefault(channelId, Set.of()).contains(viewerId);
    }

    @Override
    public long getSubscriberCount(String channelId) {
        if (isBlank(channelId)) {
            return 0;
        }
        return subscribersByChannel.getOrDefault(channelId, Set.of()).size();
    }

    public void subscribe(String viewerId, String channelId) {
        if (isBlank(viewerId) || isBlank(channelId)) {
            return;
        }
        subscribersByChannel
                .computeIfAbsent(channelId, key -> ConcurrentHashMap.newKeySet())
                .add(viewerId);
    }

    public void unsubscribe(String viewerId, String channelId) {
        if (isBlank(viewerId) || isBlank(channelId)) {
            return;
        }

        Set<String> subscribers = subscribersByChannel.get(channelId);
        if (subscribers != null) {
            subscribers.remove(viewerId);
            if (subscribers.isEmpty()) {
                subscribersByChannel.remove(channelId);
            }
        }
    }

    public void clearAll() {
        subscribersByChannel.clear();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
