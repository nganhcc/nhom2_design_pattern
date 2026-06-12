package com.group.videosharing.patterns.behavioral.command;

public class SubscribeCommand implements ICommand {
    private final String channelId;

    public SubscribeCommand(String channelId) { this.channelId = channelId; }

    @Override
    public void execute() {
        // TODO: gọi service.subscribe(channelId)
    }

    @Override
    public void undo() {
        // TODO: gọi service.unsubscribe(channelId)
    }
}
