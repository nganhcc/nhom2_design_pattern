package com.group.videosharing.patterns.structural.composite;

import java.time.LocalDateTime;

/**
 * Composite — Pattern 7.
 * ISP: addChild/removeChild KHÔNG ở đây — chỉ CommentThread mới có.
 */
public interface CommentComponent {
    void           render(int depth);
    String         getId();
    String         getAuthor();
    String         getContent();
    LocalDateTime  getTimestamp();
}
