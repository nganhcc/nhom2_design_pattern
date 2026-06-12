package com.group.videosharing.patterns.structural.proxy;

import org.springframework.stereotype.Service;

@Service
public class RealInteractionService implements IInteractionService {
    @Override public void like(String videoId)                        { /* TODO */ }
    @Override public void dislike(String videoId)                     { /* TODO */ }
    @Override public void subscribe(String channelId)                 { /* TODO */ }
    @Override public void addComment(String videoId, String text)     { /* TODO */ }
    @Override public void deleteComment(String commentId)             { /* TODO */ }
}
