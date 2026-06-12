package com.group.videosharing.patterns.behavioral.iterator;

import com.group.videosharing.patterns.structural.composite.CommentComponent;
import java.util.List;

public class CommentCollection {
    private final List<CommentComponent> roots;

    public CommentCollection(List<CommentComponent> roots) { this.roots = roots; }

    public ICommentIterator createIterator(String type) {
        return switch (type) {
            case "flat"     -> new FlatCommentIterator(roots);
            case "threaded" -> new DepthFirstCommentIterator(roots);
            default -> throw new IllegalArgumentException("Unknown iterator type: " + type);
        };
    }
}
