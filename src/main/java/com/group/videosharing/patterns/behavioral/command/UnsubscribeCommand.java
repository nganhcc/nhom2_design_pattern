package com.group.videosharing.patterns.behavioral.command;

import com.group.videosharing.patterns.structural.proxy.IInteractionService;

public class UnsubscribeCommand implements ICommand {
    private final IInteractionService interactionService;
    private final String channelId;

    public UnsubscribeCommand(IInteractionService interactionService, String channelId) {
        this.interactionService = interactionService;
        this.channelId = channelId;
    }

    @Override
    public void execute() {
        interactionService.unsubscribe(channelId);
    }

    @Override
    public void undo() {
        interactionService.subscribe(channelId);
    }
}
