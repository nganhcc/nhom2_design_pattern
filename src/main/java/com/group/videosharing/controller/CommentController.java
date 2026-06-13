package com.group.videosharing.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group.videosharing.domain.CommentEntity;
import com.group.videosharing.dto.CommentRequest;
import com.group.videosharing.service.CommentService;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService service;

    public CommentController(CommentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CommentEntity> addComment(
            @RequestBody CommentRequest request) {

        return ResponseEntity.ok(
                service.addComment(request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(
            @PathVariable String id) {

        service.deleteComment(id);

        return ResponseEntity.ok("Deleted");
    }

    @GetMapping("/video/{videoId}")
    public ResponseEntity<List<CommentEntity>> getComments(
            @PathVariable String videoId) {

        return ResponseEntity.ok(
                service.getCommentsByVideo(videoId)
        );
    }
}