package com.group.videosharing.patterns.behavioral.cor;

import com.group.videosharing.dto.CommentRequest;
import com.group.videosharing.dto.ValidationResult;

/** Chain of Responsibility — Pattern 9 */
public abstract class CommentHandler {
    private CommentHandler next;

    public CommentHandler setNext(CommentHandler handler) { this.next = handler; return handler; }

    public abstract ValidationResult handle(CommentRequest req);

    protected ValidationResult passToNext(CommentRequest req) {
        return next != null ? next.handle(req) : ValidationResult.ok();
    }
}
