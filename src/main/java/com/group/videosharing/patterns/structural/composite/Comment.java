package com.group.videosharing.patterns.structural.composite;

import java.time.LocalDateTime;

/** Leaf */
public class Comment implements CommentComponent {
    private final String        id;
    private final String        author;
    private final String        content;
    private final LocalDateTime timestamp;

    public Comment(String id, String author, String content) {
        this.id        = id;
        this.author    = author;
        this.content   = content;
        this.timestamp = LocalDateTime.now();
    }

    @Override public void          render(int depth) { System.out.println("  ".repeat(depth) + author + ": " + content); }
    @Override public String        getId()           { return id; }
    @Override public String        getAuthor()       { return author; }
    @Override public String        getContent()      { return content; }
    @Override public LocalDateTime getTimestamp()    { return timestamp; }
}
