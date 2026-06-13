package com.group.videosharing.patterns.behavioral.command;

import com.group.videosharing.patterns.structural.proxy.IInteractionService;

public class SubscribeCommand implements ICommand {
    private final IInteractionService interactionService;
    private final String channelId;

    public SubscribeCommand(IInteractionService interactionService, String channelId) {
        this.interactionService = interactionService;
        this.channelId = channelId;
    }

    @Override
    public void execute() {
        interactionService.subscribe(channelId);
    }

    @Override
    public void undo() {
        interactionService.unsubscribe(channelId);
    }
}
