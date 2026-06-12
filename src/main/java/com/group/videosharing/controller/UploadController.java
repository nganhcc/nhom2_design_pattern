package com.group.videosharing.controller;

import com.group.videosharing.dto.UploadVideoRequest;
import com.group.videosharing.dto.VideoDto;
import com.group.videosharing.service.UploadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * TV4 — VideoEntity.Builder
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping
    public ResponseEntity<?> upload(@RequestBody UploadVideoRequest request) {
        try {
            VideoDto uploadedVideo = uploadService.upload(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(uploadedVideo);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

}
