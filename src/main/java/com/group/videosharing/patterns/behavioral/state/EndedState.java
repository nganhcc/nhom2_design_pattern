package com.group.videosharing.patterns.behavioral.state;

/** Kết thúc — tự chuyển video tiếp theo */
public class EndedState implements IPlayerState {
    @Override
    public void play(VideoPlayerContext ctx) {
        ctx.setState(new PlayingState());
    }

    @Override
    public void pause(VideoPlayerContext ctx) {
        // cannot pause after ended
    }

    @Override
    public void seek(VideoPlayerContext ctx, long t) {
        ctx.setState(new PlayingState());
    }

    @Override
    public void end(VideoPlayerContext ctx) {
        // already ended
    }
}
