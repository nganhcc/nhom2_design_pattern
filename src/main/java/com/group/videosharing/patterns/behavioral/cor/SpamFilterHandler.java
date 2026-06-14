package com.group.videosharing.patterns.behavioral.cor;

import com.group.videosharing.dto.CommentRequest;
import com.group.videosharing.dto.ValidationResult;

import java.util.List;

public class SpamFilterHandler extends CommentHandler {
    private static final List<String> BLOCKED_KEYWORDS = List.of(
            "spam",
            "scam",
            "http://bad",
            "buy now"
    );

    @Override
    public ValidationResult handle(CommentRequest req) {
        String text = req.getText() == null ? "" : req.getText().toLowerCase();
        boolean spam = BLOCKED_KEYWORDS.stream().anyMatch(text::contains);
        if (spam) return ValidationResult.fail("Nội dung spam");
        return passToNext(req);
    }
}
