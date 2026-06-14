package com.group.videosharing.patterns.behavioral.cor;

import com.group.videosharing.dto.CommentRequest;
import com.group.videosharing.dto.ValidationResult;

public class VideoIdRequiredHandler extends CommentHandler {
    @Override
    public ValidationResult handle(CommentRequest req) {
        if (req.getVideoId() == null || req.getVideoId().isBlank()) {
            return ValidationResult.fail("videoId không được trống");
        }
        return passToNext(req);
    }
}
