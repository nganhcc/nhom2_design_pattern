package com.group.videosharing.patterns.behavioral.command;

public class UnsubscribeCommand implements ICommand {
    private final String channelId;

    public UnsubscribeCommand(String channelId) { this.channelId = channelId; }

    @Override
    public void execute() {
        // TODO: gọi service.unsubscribe(channelId)
    }

    @Override
    public void undo() {
        // TODO: gọi service.ununsubscribe(channelId)
    }
}
