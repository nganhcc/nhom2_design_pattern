package com.group.videosharing.patterns.behavioral.command;

import com.group.videosharing.patterns.structural.proxy.IInteractionService;

public class DislikeCommand implements ICommand {
    private final IInteractionService interactionService;
    private final String videoId;

    public DislikeCommand(IInteractionService interactionService, String videoId) {
        this.interactionService = interactionService;
        this.videoId = videoId;
    }

    @Override
    public void execute() {
        interactionService.dislike(videoId);
    }

    @Override
    public void undo() {
        interactionService.undislike(videoId);
    }
}
