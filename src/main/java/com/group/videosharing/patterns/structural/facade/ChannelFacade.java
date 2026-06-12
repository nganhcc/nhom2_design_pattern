package com.group.videosharing.patterns.structural.facade;

import com.group.videosharing.dto.ChannelPageViewModel;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Facade — Pattern 10
 * Gom 3 service, expose 1 method cho ChannelController.
 */
@Service
public class ChannelFacade {
    private final IVideoService        videoService;
    private final IUserService         userService;
    private final ISubscriptionService subscriptionService;

    public ChannelFacade(IVideoService videoService,
                         IUserService userService,
                         ISubscriptionService subscriptionService) {
        this.videoService        = videoService;
        this.userService         = userService;
        this.subscriptionService = subscriptionService;
    }

    public ChannelPageViewModel getChannelPage(String channelId, String viewerId) {
        if (channelId == null || channelId.isBlank()) {
            throw new IllegalArgumentException("channelId must not be blank");
        }

        // Gọi song song để giảm latency
        var userFuture  = CompletableFuture.supplyAsync(() -> userService.getUserById(channelId));
        var videoFuture = CompletableFuture.supplyAsync(() -> videoService.getVideosByChannel(channelId));
        var subFuture   = CompletableFuture.supplyAsync(() -> subscriptionService.isSubscribed(viewerId, channelId));

        try {
            return new ChannelPageViewModel(
                    userFuture.join(),
                    videoFuture.join(),
                    subFuture.join(),
                    subscriptionService.getSubscriberCount(channelId)
            );
        } catch (CompletionException ex) {
            throw unwrapCompletionException(ex);
        }
    }

    private RuntimeException unwrapCompletionException(CompletionException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof RuntimeException runtimeException) {
            return runtimeException;
        }
        return ex;
    }
}
