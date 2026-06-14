package com.group.videosharing.patterns.structural.composite;

import java.time.LocalDateTime;

/** Leaf */
public class Comment implements CommentComponent {
    private final String        id;
    private final String        videoId;
    private final String        authorId;
    private final String        content;
    private final String        parentId;
    private final LocalDateTime timestamp;

    public Comment(String id, String author, String content) {
        this(id, null, author, content, null, LocalDateTime.now());
    }

    public Comment(String id,
                   String videoId,
                   String authorId,
                   String content,
                   String parentId,
                   LocalDateTime timestamp) {
        this.id        = id;
        this.videoId   = videoId;
        this.authorId  = authorId;
        this.content   = content;
        this.parentId  = parentId;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    @Override public void          render(int depth) { System.out.println("  ".repeat(depth) + authorId + ": " + content); }
    @Override public String        getId()           { return id; }
    @Override public String        getVideoId()      { return videoId; }
    @Override public String        getAuthorId()     { return authorId; }
    @Override public String        getContent()      { return content; }
    @Override public String        getParentId()     { return parentId; }
    @Override public LocalDateTime getTimestamp()    { return timestamp; }
}
