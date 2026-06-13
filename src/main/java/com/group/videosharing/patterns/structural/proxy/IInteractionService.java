package com.group.videosharing.patterns.structural.proxy;

/** Proxy — Pattern 6. TV4 export interface này ngay ngày 1 sáng. */
public interface IInteractionService {
    void like(String videoId);
    void unlike(String videoId);
    void dislike(String videoId);
    void undislike(String videoId);
    void subscribe(String channelId);
    void unsubscribe(String channelId);
    void addComment(String videoId, String text);
    void deleteComment(String commentId);
}
