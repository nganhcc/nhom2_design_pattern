package com.group.videosharing.patterns.behavioral.iterator;

import com.group.videosharing.patterns.structural.composite.CommentComponent;

/** Iterator — Pattern 8 */
public interface ICommentIterator {
    boolean          hasNext();
    CommentComponent next();
}
