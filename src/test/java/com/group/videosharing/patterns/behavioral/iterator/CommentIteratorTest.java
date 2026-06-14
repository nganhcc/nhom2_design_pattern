package com.group.videosharing.patterns.behavioral.iterator;

import com.group.videosharing.patterns.structural.composite.Comment;
import com.group.videosharing.patterns.structural.composite.CommentComponent;
import com.group.videosharing.patterns.structural.composite.CommentThread;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentIteratorTest {

    @Test
    void flatIteratorReturnsBreadthFirstOrder() {
        CommentThread root = thread("root");
        CommentThread reply = thread("reply");
        CommentThread nested = thread("nested");
        reply.addChild(nested);
        root.addChild(reply);
        CommentThread secondRoot = thread("second-root");

        List<String> result = iterate("flat", List.of(root, secondRoot));

        assertEquals(List.of("root", "second-root", "reply", "nested"), result);
    }

    @Test
    void threadedIteratorReturnsDepthFirstOrderWithoutReversingRoots() {
        CommentThread root = thread("root");
        CommentThread reply = thread("reply");
        CommentThread nested = thread("nested");
        reply.addChild(nested);
        root.addChild(reply);
        CommentThread secondRoot = thread("second-root");

        List<String> result = iterate("threaded", List.of(root, secondRoot));

        assertEquals(List.of("root", "reply", "nested", "second-root"), result);
    }

    private List<String> iterate(String type, List<CommentComponent> roots) {
        ICommentIterator iterator = new CommentCollection(roots).createIterator(type);
        List<String> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(iterator.next().getId());
        }
        return result;
    }

    private CommentThread thread(String id) {
        return new CommentThread(new Comment(
                id,
                "video-1",
                "author-1",
                id,
                null,
                LocalDateTime.of(2026, 6, 14, 9, 0)));
    }
}
