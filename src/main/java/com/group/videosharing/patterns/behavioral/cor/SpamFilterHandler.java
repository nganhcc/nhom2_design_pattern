package com.group.videosharing.patterns.behavioral.cor;

import com.group.videosharing.dto.CommentRequest;
import com.group.videosharing.dto.ValidationResult;

public class SpamFilterHandler extends CommentHandler {
    @Override
    public ValidationResult handle(CommentRequest req) {
        // TODO: kiểm tra từ khóa cấm
        // if (containsSpam(req.getText())) return ValidationResult.fail("Nội dung spam");
        return passToNext(req);
    }
}
