package com.group.videosharing.patterns.behavioral.iterator;

import com.group.videosharing.patterns.structural.composite.CommentComponent;
import com.group.videosharing.patterns.structural.composite.CommentThread;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/** BFS — UI list phẳng */
public class FlatCommentIterator implements ICommentIterator {
    private final Queue<CommentComponent> queue = new LinkedList<>();

    public FlatCommentIterator(List<CommentComponent> roots) { queue.addAll(roots); }

    @Override public boolean hasNext() { return !queue.isEmpty(); }

    @Override public CommentComponent next() {
        CommentComponent c = queue.poll();
        if (c instanceof CommentThread t) t.getChildren().forEach(queue::offer);
        return c;
    }
}
