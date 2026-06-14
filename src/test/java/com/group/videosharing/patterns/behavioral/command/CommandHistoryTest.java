package com.group.videosharing.patterns.behavioral.command;

import com.group.videosharing.patterns.structural.proxy.IInteractionService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommandHistoryTest {

    @Test
    void undoLatestLikeCommandCallsUnlike() {
        FakeInteractionService interactionService = new FakeInteractionService();
        CommandHistory commandHistory = new CommandHistory();

        commandHistory.push(new LikeCommand(interactionService, "video-1"));
        commandHistory.undo();

        assertEquals(1, interactionService.likeCount);
        assertEquals(1, interactionService.unlikeCount);
    }

    @Test
    void undoLatestSubscribeCommandCallsUnsubscribe() {
        FakeInteractionService interactionService = new FakeInteractionService();
        CommandHistory commandHistory = new CommandHistory();

        commandHistory.push(new SubscribeCommand(interactionService, "channel-1"));
        commandHistory.undo();

        assertEquals(1, interactionService.subscribeCount);
        assertEquals(1, interactionService.unsubscribeCount);
    }

    private static class FakeInteractionService implements IInteractionService {
        private int likeCount;
        private int unlikeCount;
        private int subscribeCount;
        private int unsubscribeCount;

        @Override public void like(String videoId) { likeCount++; }
        @Override public void unlike(String videoId) { unlikeCount++; }
        @Override public void dislike(String videoId) {}
        @Override public void undislike(String videoId) {}
        @Override public void subscribe(String channelId) { subscribeCount++; }
        @Override public void unsubscribe(String channelId) { unsubscribeCount++; }
        @Override public void addComment(String videoId, String text) {}
        @Override public void deleteComment(String commentId) {}
    }
}
