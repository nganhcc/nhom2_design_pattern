package com.group.videosharing.patterns.behavioral.command;

import com.group.videosharing.patterns.structural.proxy.IInteractionService;

public class LikeCommand implements ICommand {
    private final IInteractionService interactionService;
    private final String videoId;

    public LikeCommand(IInteractionService interactionService, String videoId) {
        this.interactionService = interactionService;
        this.videoId = videoId;
    }

    @Override
    public void execute() {
        interactionService.like(videoId);
    }

    @Override
    public void undo() {
        interactionService.unlike(videoId);
    }
}
