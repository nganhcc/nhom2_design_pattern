package com.group.videosharing.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.group.videosharing.domain.CommentEntity;
import com.group.videosharing.dto.CommentRequest;
import com.group.videosharing.dto.ValidationResult;
import com.group.videosharing.patterns.behavioral.cor.AuthCheckHandler;
import com.group.videosharing.patterns.behavioral.cor.CommentHandler;
import com.group.videosharing.patterns.behavioral.cor.EmptyContentHandler;
import com.group.videosharing.patterns.behavioral.cor.MaxLengthHandler;
import com.group.videosharing.patterns.behavioral.cor.SpamFilterHandler;
import com.group.videosharing.patterns.behavioral.observer.CommentAddedEvent;
import com.group.videosharing.patterns.creational.singleton.EventBus;
import com.group.videosharing.patterns.creational.singleton.SessionManager;
import com.group.videosharing.repository.CommentRepository;

@Service
public class CommentService {

    private final CommentRepository repository;

    public CommentService(CommentRepository repository) {
        this.repository = repository;
    }

    public CommentEntity addComment(CommentRequest request) {

        CommentHandler chain = new AuthCheckHandler();

        chain.setNext(new EmptyContentHandler())
                .setNext(new MaxLengthHandler())
                .setNext(new SpamFilterHandler());

        ValidationResult result = chain.handle(request);

        if (!result.isValid()) {
            throw new RuntimeException(result.getError());
        }

        CommentEntity comment = new CommentEntity();

        comment.setVideoId(request.getVideoId());

        comment.setAuthorId(
                SessionManager.getInstance()
                        .getCurrentUserId()
        );

        comment.setContent(request.getText());

        comment.setParentId(request.getParentId());

        repository.save(comment);

        EventBus.getInstance()
                .publish(
                        new CommentAddedEvent(
                                request.getVideoId()
                        )
                );

        return comment;
    }

    public void deleteComment(String id) {
        repository.deleteById(id);
    }

    public List<CommentEntity> getCommentsByVideo(String videoId) {
        return repository.findByVideoId(videoId);
    }
}