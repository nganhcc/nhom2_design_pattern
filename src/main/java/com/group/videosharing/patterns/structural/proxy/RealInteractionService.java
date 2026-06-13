package com.group.videosharing.patterns.structural.proxy;

import com.group.videosharing.patterns.behavioral.observer.LikeChangedEvent;
import com.group.videosharing.patterns.behavioral.observer.SubscriptionChangedEvent;
import com.group.videosharing.patterns.behavioral.observer.VideoViewedEvent;
import com.group.videosharing.patterns.creational.singleton.EventBus;
import com.group.videosharing.patterns.creational.singleton.SessionManager;
import com.group.videosharing.service.SubscriptionService;
import com.group.videosharing.service.VideoService;
import org.springframework.stereotype.Service;

@Service
public class RealInteractionService implements IInteractionService {
    private final VideoService videoService;
    private final SubscriptionService subscriptionService;
    private final SessionManager session = SessionManager.getInstance();

    public RealInteractionService(VideoService videoService, SubscriptionService subscriptionService) {
        this.videoService = videoService;
        this.subscriptionService = subscriptionService;
    }

    @Override
    public void like(String videoId) {
        videoService.likeVideo(videoId);
    }

    @Override
    public void unlike(String videoId) {
        videoService.unlikeVideo(videoId);
    }

    @Override
    public void dislike(String videoId) {
        videoService.dislikeVideo(videoId);
    }

    @Override
    public void undislike(String videoId) {
        videoService.undislikeVideo(videoId);
    }

    @Override
    public void subscribe(String channelId) {
        String viewerId = session.getCurrentUserId();
        subscriptionService.subscribe(viewerId, channelId);
        EventBus.getInstance().publish(new SubscriptionChangedEvent(channelId, 1));
    }

    @Override
    public void unsubscribe(String channelId) {
        String viewerId = session.getCurrentUserId();
        subscriptionService.unsubscribe(viewerId, channelId);
        EventBus.getInstance().publish(new SubscriptionChangedEvent(channelId, -1));
    }

    @Override
    public void addComment(String videoId, String text) {
        // not implemented in TV2 yet
    }

    @Override
    public void deleteComment(String commentId) {
        // not implemented in TV2 yet
    }
}
