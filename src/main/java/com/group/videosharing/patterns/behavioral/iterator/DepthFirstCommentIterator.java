package com.group.videosharing.patterns.behavioral.iterator;

import com.group.videosharing.patterns.structural.composite.CommentComponent;
import com.group.videosharing.patterns.structural.composite.CommentThread;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/** DFS — thread view */
public class DepthFirstCommentIterator implements ICommentIterator {
    private final Deque<CommentComponent> stack = new ArrayDeque<>();

    public DepthFirstCommentIterator(List<CommentComponent> roots) { roots.forEach(stack::push); }

    @Override public boolean hasNext() { return !stack.isEmpty(); }

    @Override public CommentComponent next() {
        CommentComponent c = stack.pop();
        if (c instanceof CommentThread t) {
            var children = new ArrayList<>(t.getChildren());
            java.util.Collections.reverse(children);
            children.forEach(stack::push);
        }
        return c;
    }
}
