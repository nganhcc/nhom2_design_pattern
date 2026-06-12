package com.group.videosharing.patterns.behavioral.state;

public class VideoPlayerContext {
    private IPlayerState currentState = new IdleState();

    public void setState(IPlayerState state) { this.currentState = state; }
    public IPlayerState getState()           { return currentState; }

    public void play()            { currentState.play(this); }
    public void pause()           { currentState.pause(this); }
    public void seek(long timeMs) { currentState.seek(this, timeMs); }
    public void end()             { currentState.end(this); }
}
