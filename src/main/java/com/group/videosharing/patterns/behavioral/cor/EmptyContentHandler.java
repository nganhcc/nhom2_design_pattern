package com.group.videosharing.patterns.behavioral.cor;

import com.group.videosharing.dto.CommentRequest;
import com.group.videosharing.dto.ValidationResult;

public class EmptyContentHandler extends CommentHandler {
    @Override
    public ValidationResult handle(CommentRequest req) {
        if (req.getText() == null || req.getText().isBlank()) return ValidationResult.fail("Nội dung không được trống");
        return passToNext(req);
    }
}
