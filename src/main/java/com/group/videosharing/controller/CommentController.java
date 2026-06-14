package com.group.videosharing.controller;

import com.group.videosharing.dto.CommentDto;
import com.group.videosharing.dto.CommentRequest;
import com.group.videosharing.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService service;

    public CommentController(CommentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody CommentRequest request) {
        try {
            CommentDto comment = service.addComment(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(comment);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable String id) {
        try {
            service.deleteComment(id);
            return ResponseEntity.ok("Deleted");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping("/video/{videoId}")
    public ResponseEntity<?> getComments(@PathVariable String videoId,
                                         @RequestParam(required = false, defaultValue = "threaded") String view) {
        try {
            List<CommentDto> comments = service.getCommentsByVideo(videoId, view);
            return ResponseEntity.ok(comments);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
