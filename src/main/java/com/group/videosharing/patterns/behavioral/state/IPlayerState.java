package com.group.videosharing.patterns.behavioral.state;

/** State — Pattern 4 */
public interface IPlayerState {
    void play(VideoPlayerContext ctx);
    void pause(VideoPlayerContext ctx);
    void seek(VideoPlayerContext ctx, long timeMs);
    void end(VideoPlayerContext ctx);
}
