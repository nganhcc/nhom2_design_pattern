package com.group.videosharing.patterns.behavioral.command;

public class LikeCommand implements ICommand {
    private final String videoId;

    public LikeCommand(String videoId) { this.videoId = videoId; }

    @Override
    public void execute() {
        // TODO: gọi service.like(videoId)
    }

    @Override
    public void undo() {
        // TODO: gọi service.unlike(videoId)
    }
}
