package com.group.videosharing.patterns.behavioral.command;

import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;

@Component
public class CommandHistory {
    private final Deque<ICommand> stack = new ArrayDeque<>();

    public void push(ICommand cmd) {
        cmd.execute();
        stack.push(cmd);
    }

    public void undo() {
        if (!stack.isEmpty()) stack.pop().undo();
    }
}
