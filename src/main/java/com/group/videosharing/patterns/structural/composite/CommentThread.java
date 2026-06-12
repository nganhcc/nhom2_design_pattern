package com.group.videosharing.patterns.structural.composite;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Composite */
public class CommentThread implements CommentComponent {
    private final Comment                root;
    private final List<CommentComponent> children = new ArrayList<>();

    public CommentThread(Comment root) { this.root = root; }

    public void addChild(CommentComponent c)    { children.add(c); }
    public void removeChild(String id)          { children.removeIf(c -> c.getId().equals(id)); }
    public List<CommentComponent> getChildren() { return children; }

    @Override public void          render(int depth) { root.render(depth); children.forEach(c -> c.render(depth + 1)); }
    @Override public String        getId()           { return root.getId(); }
    @Override public String        getAuthor()       { return root.getAuthor(); }
    @Override public String        getContent()      { return root.getContent(); }
    @Override public LocalDateTime getTimestamp()    { return root.getTimestamp(); }
}
