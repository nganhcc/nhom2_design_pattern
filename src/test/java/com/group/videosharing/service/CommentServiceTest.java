package com.group.videosharing.service;

import com.group.videosharing.domain.CommentEntity;
import com.group.videosharing.dto.CommentDto;
import com.group.videosharing.dto.CommentRequest;
import com.group.videosharing.dto.UserDto;
import com.group.videosharing.patterns.behavioral.observer.CommentAddedEvent;
import com.group.videosharing.patterns.creational.singleton.EventBus;
import com.group.videosharing.patterns.creational.singleton.SessionManager;
import com.group.videosharing.repository.CommentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommentServiceTest {

    private FakeCommentRepository commentRepository;
    private CommentService commentService;
    private SessionManager sessionManager;
    private EventBus eventBus;

    @BeforeEach
    void setUp() {
        commentRepository = new FakeCommentRepository();
        commentService = new CommentService(commentRepository.proxy());
        sessionManager = SessionManager.getInstance();
        sessionManager.logout();
        eventBus = EventBus.getInstance();
        eventBus.clearAllHandlers();
    }

    @AfterEach
    void tearDown() {
        sessionManager.logout();
        eventBus.clearAllHandlers();
    }

    @Test
    void addCommentRejectsWhenLoggedOut() {
        assertThrows(IllegalStateException.class, () -> commentService.addComment(request("video-1", "Hello", null)));
    }

    @Test
    void addCommentRejectsInvalidContent() {
        login("user-1");

        assertThrows(IllegalArgumentException.class, () -> commentService.addComment(request("", "Hello", null)));
        assertThrows(IllegalArgumentException.class, () -> commentService.addComment(request("video-1", " ", null)));
        assertThrows(IllegalArgumentException.class, () -> commentService.addComment(request("video-1", "x".repeat(501), null)));
        assertThrows(IllegalArgumentException.class, () -> commentService.addComment(request("video-1", "buy now please", null)));
    }

    @Test
    void addTopLevelCommentUsesSessionUserAndPublishesEvent() {
        login("author-1");
        List<CommentAddedEvent> events = new ArrayList<>();
        eventBus.subscribe(CommentAddedEvent.class, events::add);

        CommentDto result = commentService.addComment(request("video-1", "Bài này dễ hiểu", null));

        assertEquals("comment-1", result.getId());
        assertEquals("video-1", result.getVideoId());
        assertEquals("author-1", result.getAuthorId());
        assertEquals("Bài này dễ hiểu", result.getText());
        assertEquals("video-1", events.getFirst().videoId());
    }

    @Test
    void addReplyRequiresParentInSameVideo() {
        login("author-1");
        CommentEntity parent = entity("parent-1", "video-1", "author-2", "Parent", null, 1);
        CommentEntity otherVideoParent = entity("parent-2", "video-2", "author-2", "Other", null, 2);
        commentRepository.put(parent);
        commentRepository.put(otherVideoParent);

        CommentDto reply = commentService.addComment(request("video-1", "Reply", "parent-1"));

        assertEquals("parent-1", reply.getParentId());
        assertThrows(IllegalArgumentException.class,
                () -> commentService.addComment(request("video-1", "Reply", "missing")));
        assertThrows(IllegalArgumentException.class,
                () -> commentService.addComment(request("video-1", "Reply", "parent-2")));
    }

    @Test
    void getCommentsFlatReturnsBreadthFirstOrder() {
        seedCommentTree();

        List<CommentDto> result = commentService.getCommentsByVideo("video-1", "flat");

        assertEquals(List.of("root-1", "root-2", "reply-1", "reply-2", "nested-1"),
                result.stream().map(CommentDto::getId).toList());
        assertEquals(List.of(), result.getFirst().getReplies());
    }

    @Test
    void getCommentsThreadedReturnsNestedReplies() {
        seedCommentTree();

        List<CommentDto> result = commentService.getCommentsByVideo("video-1", "threaded");

        assertEquals(List.of("root-1", "root-2"), result.stream().map(CommentDto::getId).toList());
        CommentDto root = result.getFirst();
        assertEquals(List.of("reply-1", "reply-2"), root.getReplies().stream().map(CommentDto::getId).toList());
        assertEquals(List.of("nested-1"), root.getReplies().getFirst().getReplies().stream().map(CommentDto::getId).toList());
    }

    @Test
    void getCommentsRejectsInvalidView() {
        assertThrows(IllegalArgumentException.class, () -> commentService.getCommentsByVideo("video-1", "grid"));
    }

    @Test
    void deleteParentCommentDeletesSubtree() {
        seedCommentTree();

        commentService.deleteComment("root-1");

        assertEquals(List.of("root-1", "reply-1", "reply-2", "nested-1"), commentRepository.deletedIds);
        assertEquals(List.of("root-2"), commentRepository.store.keySet().stream().toList());
    }

    @Test
    void deleteMissingCommentThrowsNotFound() {
        assertThrows(NoSuchElementException.class, () -> commentService.deleteComment("missing"));
    }

    private void seedCommentTree() {
        commentRepository.put(entity("root-1", "video-1", "author-1", "Root 1", null, 1));
        commentRepository.put(entity("root-2", "video-1", "author-2", "Root 2", null, 2));
        commentRepository.put(entity("reply-1", "video-1", "author-3", "Reply 1", "root-1", 3));
        commentRepository.put(entity("reply-2", "video-1", "author-4", "Reply 2", "root-1", 4));
        commentRepository.put(entity("nested-1", "video-1", "author-5", "Nested", "reply-1", 5));
    }

    private CommentRequest request(String videoId, String text, String parentId) {
        CommentRequest request = new CommentRequest();
        request.setVideoId(videoId);
        request.setText(text);
        request.setParentId(parentId);
        request.setLoggedIn(false);
        return request;
    }

    private void login(String id) {
        UserDto user = new UserDto();
        user.setId(id);
        user.setChannelId(id);
        sessionManager.login(user);
    }

    private CommentEntity entity(String id, String videoId, String authorId, String content, String parentId, int minute) {
        CommentEntity entity = new CommentEntity();
        ReflectionTestUtils.setField(entity, "id", id);
        entity.setVideoId(videoId);
        entity.setAuthorId(authorId);
        entity.setContent(content);
        entity.setParentId(parentId);
        entity.setCreatedAt(LocalDateTime.of(2026, 6, 14, 9, minute));
        return entity;
    }

    private static class FakeCommentRepository {
        private final Map<String, CommentEntity> store = new LinkedHashMap<>();
        private final List<String> deletedIds = new ArrayList<>();
        private int nextId = 1;

        private void put(CommentEntity comment) {
            store.put(comment.getId(), comment);
        }

        private CommentRepository proxy() {
            return (CommentRepository) Proxy.newProxyInstance(
                    CommentRepository.class.getClassLoader(),
                    new Class<?>[]{CommentRepository.class},
                    (target, method, args) -> {
                        if (method.getName().equals("save")) {
                            CommentEntity comment = (CommentEntity) args[0];
                            if (comment.getId() == null) {
                                ReflectionTestUtils.setField(comment, "id", "comment-" + nextId++);
                            }
                            store.put(comment.getId(), comment);
                            return comment;
                        }
                        if (method.getName().equals("findById")) {
                            return Optional.ofNullable(store.get((String) args[0]));
                        }
                        if (method.getName().equals("findByVideoId")) {
                            String videoId = (String) args[0];
                            return store.values().stream()
                                    .filter(comment -> videoId.equals(comment.getVideoId()))
                                    .toList();
                        }
                        if (method.getName().equals("findByParentId")) {
                            String parentId = (String) args[0];
                            return store.values().stream()
                                    .filter(comment -> parentId.equals(comment.getParentId()))
                                    .toList();
                        }
                        if (method.getName().equals("deleteAll")) {
                            Iterable<?> comments = (Iterable<?>) args[0];
                            for (Object item : comments) {
                                CommentEntity comment = (CommentEntity) item;
                                deletedIds.add(comment.getId());
                                store.remove(comment.getId());
                            }
                            return null;
                        }
                        if (method.getName().equals("toString")) {
                            return "FakeCommentRepository";
                        }
                        throw new UnsupportedOperationException("Unsupported method: " + method.getName());
                    });
        }
    }
}
