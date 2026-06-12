package com.group.videosharing.patterns.behavioral.cor;

import com.group.videosharing.dto.CommentRequest;
import com.group.videosharing.dto.ValidationResult;

public class MaxLengthHandler extends CommentHandler {
    @Override
    public ValidationResult handle(CommentRequest req) {
        if (req.getText().length() > 500) return ValidationResult.fail("Vượt quá 500 ký tự");
        return passToNext(req);
    }
}
