package com.group.videosharing.service;

import com.group.videosharing.domain.VideoEntity;
import com.group.videosharing.dto.UploadVideoRequest;
import com.group.videosharing.dto.VideoDto;
import com.group.videosharing.patterns.behavioral.observer.VideoUploadedEvent;
import com.group.videosharing.patterns.creational.singleton.EventBus;
import com.group.videosharing.repository.VideoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UploadServiceTest {

    private FakeVideoRepository videoRepository;
    private UploadService uploadService;
    private EventBus eventBus;
    private List<VideoUploadedEvent> publishedEvents;

    @BeforeEach
    void setUp() {
        videoRepository = new FakeVideoRepository();
        uploadService = new UploadService(videoRepository.proxy(), new VideoMapper());
        eventBus = EventBus.getInstance();
        eventBus.clearAllHandlers();
        publishedEvents = new ArrayList<>();
        eventBus.subscribe(VideoUploadedEvent.class, publishedEvents::add);
    }

    @AfterEach
    void tearDown() {
        eventBus.clearAllHandlers();
    }

    @Test
    void uploadValidRequestSavesVideoReturnsDtoAndPublishesEvent() {
        VideoDto result = uploadService.upload(validRequest());

        assertEquals(1, videoRepository.saveCount);
        assertSame(videoRepository.savedVideo, publishedEvents.getFirst().video());
        assertEquals("saved-video-1", result.getId());
        assertEquals("Design Patterns Upload", result.getTitle());
        assertEquals("Upload metadata demo", result.getDescription());
        assertEquals("user-1", result.getUploaderId());
        assertEquals("user-1", result.getChannelId());
        assertEquals("public", result.getVisibility());
        assertEquals(240, result.getDuration());
        assertEquals("Education", result.getCategory());
        assertEquals("https://example.com/thumb.jpg", result.getThumbnailUrl());
        assertEquals("2026-06-12T11:45", result.getCreatedAt());
    }

    @Test
    void uploadNullRequestThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> uploadService.upload(null));
    }

    @Test
    void uploadBlankTitleThrowsIllegalArgumentException() {
        UploadVideoRequest request = validRequest();
        request.setTitle(" ");

        assertThrows(IllegalArgumentException.class, () -> uploadService.upload(request));
    }

    @Test
    void uploadInvalidVisibilityThrowsIllegalArgumentException() {
        UploadVideoRequest request = validRequest();
        request.setVisibility("friends");

        assertThrows(IllegalArgumentException.class, () -> uploadService.upload(request));
    }

    @Test
    void uploadBlankUploaderIdThrowsIllegalArgumentException() {
        UploadVideoRequest request = validRequest();
        request.setUploaderId("");

        assertThrows(IllegalArgumentException.class, () -> uploadService.upload(request));
    }

    @Test
    void uploadBlankCategoryKeepsDefaultGeneral() {
        UploadVideoRequest request = validRequest();
        request.setCategory(" ");

        VideoDto result = uploadService.upload(request);

        assertEquals("General", result.getCategory());
    }

    @Test
    void uploadNullDurationKeepsDefaultZero() {
        UploadVideoRequest request = validRequest();
        request.setDurationSeconds(null);

        VideoDto result = uploadService.upload(request);

        assertEquals(0, result.getDuration());
    }

    private UploadVideoRequest validRequest() {
        UploadVideoRequest request = new UploadVideoRequest();
        request.setTitle("Design Patterns Upload");
        request.setDescription("Upload metadata demo");
        request.setThumbnailUrl("https://example.com/thumb.jpg");
        request.setVisibility("public");
        request.setUploaderId("user-1");
        request.setVideoUrl("https://example.com/video.mp4");
        request.setDurationSeconds(240);
        request.setCategory("Education");
        return request;
    }

    private static class FakeVideoRepository {
        private int saveCount;
        private VideoEntity savedVideo;

        private VideoRepository proxy() {
            return (VideoRepository) Proxy.newProxyInstance(
                    VideoRepository.class.getClassLoader(),
                    new Class<?>[]{VideoRepository.class},
                    (target, method, args) -> {
                        if (method.getName().equals("save")) {
                            saveCount++;
                            savedVideo = (VideoEntity) args[0];
                            ReflectionTestUtils.setField(savedVideo, "id", "saved-video-1");
                            ReflectionTestUtils.setField(
                                    savedVideo,
                                    "uploadedAt",
                                    LocalDateTime.of(2026, 6, 12, 11, 45));
                            return savedVideo;
                        }
                        if (method.getName().equals("toString")) {
                            return "FakeVideoRepository";
                        }
                        throw new UnsupportedOperationException("Unsupported method: " + method.getName());
                    });
        }
    }
}
