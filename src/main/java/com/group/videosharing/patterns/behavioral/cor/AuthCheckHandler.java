package com.group.videosharing.patterns.behavioral.cor;

import com.group.videosharing.dto.CommentRequest;
import com.group.videosharing.dto.ValidationResult;

public class AuthCheckHandler extends CommentHandler {
    @Override
    public ValidationResult handle(CommentRequest req) {
        if (!req.isLoggedIn()) return ValidationResult.fail("Chưa đăng nhập");
        return passToNext(req);
    }
}
