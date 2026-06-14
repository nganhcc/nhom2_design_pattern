package com.group.videosharing.service;

import com.group.videosharing.domain.CommentEntity;
import com.group.videosharing.dto.CommentDto;
import com.group.videosharing.dto.CommentRequest;
import com.group.videosharing.dto.ValidationResult;
import com.group.videosharing.patterns.behavioral.cor.AuthCheckHandler;
import com.group.videosharing.patterns.behavioral.cor.CommentHandler;
import com.group.videosharing.patterns.behavioral.cor.EmptyContentHandler;
import com.group.videosharing.patterns.behavioral.cor.MaxLengthHandler;
import com.group.videosharing.patterns.behavioral.cor.SpamFilterHandler;
import com.group.videosharing.patterns.behavioral.cor.VideoIdRequiredHandler;
import com.group.videosharing.patterns.behavioral.iterator.CommentCollection;
import com.group.videosharing.patterns.behavioral.iterator.ICommentIterator;
import com.group.videosharing.patterns.behavioral.observer.CommentAddedEvent;
import com.group.videosharing.patterns.creational.singleton.EventBus;
import com.group.videosharing.patterns.creational.singleton.SessionManager;
import com.group.videosharing.patterns.structural.composite.Comment;
import com.group.videosharing.patterns.structural.composite.CommentComponent;
import com.group.videosharing.patterns.structural.composite.CommentThread;
import com.group.videosharing.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class CommentService {

    private static final String FLAT_VIEW = "flat";
    private static final String THREADED_VIEW = "threaded";

    private final CommentRepository repository;

    public CommentService(CommentRepository repository) {
        this.repository = repository;
    }

    public CommentDto addComment(CommentRequest request) {
        validateRequest(request);

        String videoId = request.getVideoId().trim();
        if (!isBlank(request.getParentId())) {
            CommentEntity parent = repository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("parentId không tồn tại"));
            if (!videoId.equals(parent.getVideoId())) {
                throw new IllegalArgumentException("parentId không thuộc videoId");
            }
        }

        CommentEntity comment = new CommentEntity();
        comment.setVideoId(videoId);
        comment.setAuthorId(SessionManager.getInstance().getCurrentUserId());
        comment.setContent(request.getText().trim());
        comment.setParentId(isBlank(request.getParentId()) ? null : request.getParentId().trim());

        CommentEntity savedComment = repository.save(comment);
        EventBus.getInstance().publish(new CommentAddedEvent(savedComment.getVideoId()));
        return toDto(savedComment);
    }

    public void deleteComment(String id) {
        if (isBlank(id)) {
            throw new IllegalArgumentException("commentId must not be blank");
        }

        CommentEntity root = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Comment not found: " + id));
        List<CommentEntity> commentsInVideo = repository.findByVideoId(root.getVideoId());
        Set<String> idsToDelete = collectSubtreeIds(root.getId(), commentsInVideo);
        List<CommentEntity> commentsToDelete = commentsInVideo.stream()
                .filter(comment -> idsToDelete.contains(comment.getId()))
                .toList();
        repository.deleteAll(commentsToDelete);
    }

    public List<CommentDto> getCommentsByVideo(String videoId, String view) {
        validateVideoId(videoId);
        String normalizedView = normalizeView(view);
        List<CommentComponent> roots = buildCommentTree(repository.findByVideoId(videoId.trim()));

        if (FLAT_VIEW.equals(normalizedView)) {
            ICommentIterator iterator = new CommentCollection(roots).createIterator(FLAT_VIEW);
            List<CommentDto> results = new ArrayList<>();
            while (iterator.hasNext()) {
                results.add(toFlatDto(iterator.next()));
            }
            return results;
        }

        return roots.stream()
                .map(this::toThreadedDto)
                .toList();
    }

    private void validateRequest(CommentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }

        CommentHandler chain = new AuthCheckHandler();
        chain.setNext(new VideoIdRequiredHandler())
                .setNext(new EmptyContentHandler())
                .setNext(new MaxLengthHandler())
                .setNext(new SpamFilterHandler());

        ValidationResult result = chain.handle(request);
        if (!result.isValid()) {
            if ("Chưa đăng nhập".equals(result.getError())) {
                throw new IllegalStateException(result.getError());
            }
            throw new IllegalArgumentException(result.getError());
        }
    }

    private void validateVideoId(String videoId) {
        if (isBlank(videoId)) {
            throw new IllegalArgumentException("videoId không được trống");
        }
    }

    private String normalizeView(String view) {
        if (isBlank(view)) {
            return THREADED_VIEW;
        }

        String normalizedView = view.trim().toLowerCase();
        if (!FLAT_VIEW.equals(normalizedView) && !THREADED_VIEW.equals(normalizedView)) {
            throw new IllegalArgumentException("view must be flat or threaded");
        }
        return normalizedView;
    }

    private List<CommentComponent> buildCommentTree(List<CommentEntity> comments) {
        List<CommentEntity> sortedComments = comments.stream()
                .sorted(Comparator
                        .comparing(CommentEntity::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(CommentEntity::getId, Comparator.nullsLast(String::compareTo)))
                .toList();

        Map<String, CommentThread> threadsById = new LinkedHashMap<>();
        for (CommentEntity comment : sortedComments) {
            threadsById.put(comment.getId(), new CommentThread(toCompositeComment(comment)));
        }

        List<CommentComponent> roots = new ArrayList<>();
        for (CommentEntity comment : sortedComments) {
            CommentThread thread = threadsById.get(comment.getId());
            CommentThread parent = threadsById.get(comment.getParentId());
            if (isBlank(comment.getParentId()) || parent == null) {
                roots.add(thread);
            } else {
                parent.addChild(thread);
            }
        }
        return roots;
    }

    private Comment toCompositeComment(CommentEntity entity) {
        return new Comment(
                entity.getId(),
                entity.getVideoId(),
                entity.getAuthorId(),
                entity.getContent(),
                entity.getParentId(),
                entity.getCreatedAt());
    }

    private CommentDto toDto(CommentEntity entity) {
        CommentDto dto = new CommentDto();
        dto.setId(entity.getId());
        dto.setVideoId(entity.getVideoId());
        dto.setAuthorId(entity.getAuthorId());
        dto.setText(entity.getContent());
        dto.setParentId(entity.getParentId());
        dto.setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null);
        dto.setReplies(List.of());
        return dto;
    }

    private CommentDto toFlatDto(CommentComponent component) {
        CommentDto dto = toDto(component);
        dto.setReplies(List.of());
        return dto;
    }

    private CommentDto toThreadedDto(CommentComponent component) {
        CommentDto dto = toDto(component);
        if (component instanceof CommentThread thread) {
            dto.setReplies(thread.getChildren().stream()
                    .map(this::toThreadedDto)
                    .toList());
        } else {
            dto.setReplies(List.of());
        }
        return dto;
    }

    private CommentDto toDto(CommentComponent component) {
        CommentDto dto = new CommentDto();
        dto.setId(component.getId());
        dto.setVideoId(component.getVideoId());
        dto.setAuthorId(component.getAuthorId());
        dto.setText(component.getContent());
        dto.setParentId(component.getParentId());
        dto.setCreatedAt(component.getTimestamp() != null ? component.getTimestamp().toString() : null);
        return dto;
    }

    private Set<String> collectSubtreeIds(String rootId, List<CommentEntity> comments) {
        Set<String> idsToDelete = new HashSet<>();
        collectSubtreeIds(rootId, comments, idsToDelete);
        return idsToDelete;
    }

    private void collectSubtreeIds(String id, List<CommentEntity> comments, Set<String> idsToDelete) {
        idsToDelete.add(id);
        comments.stream()
                .filter(comment -> id.equals(comment.getParentId()))
                .forEach(comment -> collectSubtreeIds(comment.getId(), comments, idsToDelete));
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
