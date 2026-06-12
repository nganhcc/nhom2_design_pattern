package com.group.videosharing.controller;

import com.group.videosharing.domain.VideoEntity;
import com.group.videosharing.dto.UploadVideoRequest;
import com.group.videosharing.dto.VideoDto;
import com.group.videosharing.repository.VideoRepository;
import com.group.videosharing.service.UploadService;
import com.group.videosharing.service.VideoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Proxy;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UploadControllerTest {

    private UploadController uploadController;

    @BeforeEach
    void setUp() {
        uploadController = new UploadController(new UploadService(fakeRepository(), new VideoMapper()));
    }

    @Test
    void uploadValidRequestReturnsCreatedWithVideoDto() {
        ResponseEntity<?> response = uploadController.upload(validRequest());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        VideoDto body = (VideoDto) response.getBody();
        assertEquals("saved-video-1", body.getId());
        assertEquals("Design Patterns Upload", body.getTitle());
        assertEquals("user-1", body.getUploaderId());
    }

    @Test
    void uploadInvalidRequestReturnsBadRequest() {
        UploadVideoRequest request = validRequest();
        request.setTitle("");

        ResponseEntity<?> response = uploadController.upload(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Title không được trống", response.getBody());
    }

    private UploadVideoRequest validRequest() {
        UploadVideoRequest request = new UploadVideoRequest();
        request.setTitle("Design Patterns Upload");
        request.setVisibility("public");
        request.setUploaderId("user-1");
        return request;
    }

    private VideoRepository fakeRepository() {
        return (VideoRepository) Proxy.newProxyInstance(
                VideoRepository.class.getClassLoader(),
                new Class<?>[]{VideoRepository.class},
                (target, method, args) -> {
                    if (method.getName().equals("save")) {
                        VideoEntity savedVideo = (VideoEntity) args[0];
                        ReflectionTestUtils.setField(savedVideo, "id", "saved-video-1");
                        ReflectionTestUtils.setField(
                                savedVideo,
                                "uploadedAt",
                                LocalDateTime.of(2026, 6, 12, 12, 0));
                        return savedVideo;
                    }
                    if (method.getName().equals("toString")) {
                        return "FakeVideoRepository";
                    }
                    throw new UnsupportedOperationException("Unsupported method: " + method.getName());
                });
    }
}
