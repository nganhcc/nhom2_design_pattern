package com.group.videosharing.patterns.structural.facade;

public interface ISubscriptionService {
    boolean isSubscribed(String viewerId, String channelId);
    long    getSubscriberCount(String channelId);
}
