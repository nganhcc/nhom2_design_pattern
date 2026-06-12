package com.group.videosharing.patterns.behavioral.command;

public class DislikeCommand implements ICommand {
    private final String videoId;

    public DislikeCommand(String videoId) { this.videoId = videoId; }

    @Override
    public void execute() {
        // TODO: gọi service.dislike(videoId)
    }

    @Override
    public void undo() {
        // TODO: gọi service.undislike(videoId)
    }
}
