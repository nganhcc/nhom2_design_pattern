package com.group.videosharing.patterns.behavioral.state;

/** Đang buffer — disable seek/pause, hiển thị spinner */
public class LoadingState implements IPlayerState {
    @Override
    public void play(VideoPlayerContext ctx) {
        ctx.setState(new PlayingState());
    }

    @Override
    public void pause(VideoPlayerContext ctx) {
        ctx.setState(new PausedState());
    }

    @Override
    public void seek(VideoPlayerContext ctx, long t) {
        // keep loading until play or pause
    }

    @Override
    public void end(VideoPlayerContext ctx) {
        ctx.setState(new EndedState());
    }
}
