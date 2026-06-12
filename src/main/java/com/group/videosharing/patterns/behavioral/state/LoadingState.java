package com.group.videosharing.patterns.behavioral.state;

/** Đang buffer — disable seek/pause, hiển thị spinner */
public class LoadingState implements IPlayerState {
    @Override public void play(VideoPlayerContext ctx)            { /* TODO */ }
    @Override public void pause(VideoPlayerContext ctx)           { /* TODO */ }
    @Override public void seek(VideoPlayerContext ctx, long t)    { /* TODO */ }
    @Override public void end(VideoPlayerContext ctx)             { /* TODO */ }
}
