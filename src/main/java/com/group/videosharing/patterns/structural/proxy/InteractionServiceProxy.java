package com.group.videosharing.patterns.structural.proxy;

import com.group.videosharing.patterns.creational.singleton.SessionManager;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class InteractionServiceProxy implements IInteractionService {
    private final RealInteractionService real;
    private final SessionManager         session = SessionManager.getInstance();

    public InteractionServiceProxy(RealInteractionService real) { this.real = real; }

    private void checkAuth() {
        session.requireLogin();
    }

    @Override public void like(String videoId)                    { checkAuth(); real.like(videoId); }
    @Override public void unlike(String videoId)                  { checkAuth(); real.unlike(videoId); }
    @Override public void dislike(String videoId)                 { checkAuth(); real.dislike(videoId); }
    @Override public void undislike(String videoId)               { checkAuth(); real.undislike(videoId); }
    @Override public void subscribe(String channelId)             { checkAuth(); real.subscribe(channelId); }
    @Override public void unsubscribe(String channelId)           { checkAuth(); real.unsubscribe(channelId); }
    @Override public void addComment(String videoId, String text) { checkAuth(); real.addComment(videoId, text); }
    @Override public void deleteComment(String commentId)         { checkAuth(); real.deleteComment(commentId); }
}
