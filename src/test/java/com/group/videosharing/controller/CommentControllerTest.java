package com.group.videosharing.controller;

import com.group.videosharing.dto.CommentDto;
import com.group.videosharing.dto.CommentRequest;
import com.group.videosharing.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentControllerTest {

    @Test
    void addCommentReturnsCreated() {
        CommentController controller = new CommentController(new FakeCommentService());

        ResponseEntity<?> response = controller.addComment(request("video-1", "Hay", null));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("comment-1", ((CommentDto) response.getBody()).getId());
    }

    @Test
    void addCommentReturnsBadRequestForValidationFailure() {
        CommentController controller = new CommentController(new FakeCommentService("bad-request"));

        ResponseEntity<?> response = controller.addComment(request("video-1", "", null));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void addCommentReturnsUnauthorizedWhenLoggedOut() {
        CommentController controller = new CommentController(new FakeCommentService("unauthorized"));

        ResponseEntity<?> response = controller.addComment(request("video-1", "Hay", null));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void deleteMissingCommentReturnsNotFound() {
        CommentController controller = new CommentController(new FakeCommentService("missing"));

        ResponseEntity<?> response = controller.deleteComment("missing");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getCommentsReturnsOkForFlatAndThreadedViews() {
        CommentController controller = new CommentController(new FakeCommentService());

        ResponseEntity<?> flat = controller.getComments("video-1", "flat");
        ResponseEntity<?> threaded = controller.getComments("video-1", "threaded");

        assertEquals(HttpStatus.OK, flat.getStatusCode());
        assertEquals(HttpStatus.OK, threaded.getStatusCode());
    }

    @Test
    void getCommentsReturnsBadRequestForInvalidView() {
        CommentController controller = new CommentController(new FakeCommentService("bad-request"));

        ResponseEntity<?> response = controller.getComments("video-1", "grid");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private CommentRequest request(String videoId, String text, String parentId) {
        CommentRequest request = new CommentRequest();
        request.setVideoId(videoId);
        request.setText(text);
        request.setParentId(parentId);
        return request;
    }

    private static class FakeCommentService extends CommentService {
        private final String mode;

        private FakeCommentService() {
            this(null);
        }

        private FakeCommentService(String mode) {
            super(null);
            this.mode = mode;
        }

        @Override
        public CommentDto addComment(CommentRequest request) {
            if ("bad-request".equals(mode)) {
                throw new IllegalArgumentException("bad request");
            }
            if ("unauthorized".equals(mode)) {
                throw new IllegalStateException("Chưa đăng nhập");
            }
            CommentDto dto = new CommentDto();
            dto.setId("comment-1");
            return dto;
        }

        @Override
        public void deleteComment(String id) {
            if ("missing".equals(mode)) {
                throw new NoSuchElementException("Comment not found: " + id);
            }
        }

        @Override
        public List<CommentDto> getCommentsByVideo(String videoId, String view) {
            if ("bad-request".equals(mode)) {
                throw new IllegalArgumentException("view must be flat or threaded");
            }
            CommentDto dto = new CommentDto();
            dto.setId("comment-1");
            return List.of(dto);
        }
    }
}
