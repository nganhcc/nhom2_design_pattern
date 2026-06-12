package com.group.videosharing.patterns.structural.proxy;

import com.group.videosharing.patterns.creational.singleton.SessionManager;
import org.springframework.stereotype.Service;

@Service
public class InteractionServiceProxy implements IInteractionService {
    private final RealInteractionService real;
    private final SessionManager         session = SessionManager.getInstance();

    public InteractionServiceProxy(RealInteractionService real) { this.real = real; }

    private void checkAuth() {
        session.requireLogin();
    }

    @Override public void like(String videoId)                    { checkAuth(); real.like(videoId); }
    @Override public void dislike(String videoId)                 { checkAuth(); real.dislike(videoId); }
    @Override public void subscribe(String channelId)             { checkAuth(); real.subscribe(channelId); }
    @Override public void addComment(String videoId, String text) { checkAuth(); real.addComment(videoId, text); }
    @Override public void deleteComment(String commentId)         { checkAuth(); real.deleteComment(commentId); }
}
