package com.group.videosharing.patterns.behavioral.state;

/** Đang phát */
public class PlayingState implements IPlayerState {
    @Override
    public void play(VideoPlayerContext ctx) {
        // already playing
    }

    @Override
    public void pause(VideoPlayerContext ctx) {
        ctx.setState(new PausedState());
    }

    @Override
    public void seek(VideoPlayerContext ctx, long t) {
        ctx.setState(new PlayingState());
    }

    @Override
    public void end(VideoPlayerContext ctx) {
        ctx.setState(new EndedState());
    }
}
