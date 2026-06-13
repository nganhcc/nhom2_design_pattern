package com.group.videosharing.patterns.behavioral.state;

/** Chưa load — disable mọi control */
public class IdleState implements IPlayerState {
    @Override
    public void play(VideoPlayerContext ctx) {
        ctx.setState(new LoadingState());
    }

    @Override
    public void pause(VideoPlayerContext ctx) {
        // nothing to pause while idle
    }

    @Override
    public void seek(VideoPlayerContext ctx, long t) {
        // cannot seek while idle
    }

    @Override
    public void end(VideoPlayerContext ctx) {
        // no effect in idle
    }
}
