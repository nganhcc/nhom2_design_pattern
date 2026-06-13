package com.group.videosharing.patterns.behavioral.state;

/** Đã tạm dừng */
public class PausedState implements IPlayerState {
    @Override
    public void play(VideoPlayerContext ctx) {
        ctx.setState(new PlayingState());
    }

    @Override
    public void pause(VideoPlayerContext ctx) {
        // already paused
    }

    @Override
    public void seek(VideoPlayerContext ctx, long t) {
        ctx.setState(new PausedState());
    }

    @Override
    public void end(VideoPlayerContext ctx) {
        ctx.setState(new EndedState());
    }
}
