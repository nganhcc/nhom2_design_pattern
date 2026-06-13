package com.group.videosharing.service;

import com.group.videosharing.domain.VideoEntity;
import com.group.videosharing.dto.VideoDto;
import com.group.videosharing.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VideoServiceTest {

    private FakeVideoRepository videoRepository;
    private VideoService videoService;

    @BeforeEach
    void setUp() {
        videoRepository = new FakeVideoRepository();
        videoService = new VideoService(videoRepository.proxy(), new VideoMapper());
    }

    @Test
    void getVideosByChannelLoadsPublicVideosAndMapsDto() {
        VideoEntity video = video("video-1", "channel-1");
        videoRepository.videosByChannel = List.of(video);

        List<VideoDto> result = videoService.getVideosByChannel("channel-1");

        assertEquals("channel-1", videoRepository.lastUploaderId);
        assertEquals("public", videoRepository.lastVisibility);
        assertEquals(1, result.size());
        assertVideoDto(result.getFirst());
    }

    @Test
    void getVideoByIdReturnsMappedDtoWhenFound() {
        videoRepository.videoById = Optional.of(video("video-1", "channel-1"));

        VideoDto result = videoService.getVideoById("video-1");

        assertEquals("video-1", videoRepository.lastFindById);
        assertVideoDto(result);
    }

    @Test
    void getVideosByChannelRejectsBlankId() {
        assertThrows(IllegalArgumentException.class, () -> videoService.getVideosByChannel(" "));
    }

    @Test
    void getVideoByIdRejectsBlankId() {
        assertThrows(IllegalArgumentException.class, () -> videoService.getVideoById(""));
    }

    @Test
    void getVideoByIdThrowsWhenVideoDoesNotExist() {
        videoRepository.videoById = Optional.empty();

        assertThrows(NoSuchElementException.class, () -> videoService.getVideoById("missing"));
    }

    @Test
    void recordViewIncrementsViewCountAndPublishesEvent() {
        VideoEntity video = video("video-1", "channel-1");
        ReflectionTestUtils.setField(video, "viewCount", 5L);
        videoRepository.videoById = Optional.of(video);

        VideoDto result = videoService.recordView("video-1");

        assertEquals(6L, result.getViewCount());
        assertEquals(6L, ReflectionTestUtils.getField(video, "viewCount"));
        assertEquals("video-1", videoRepository.lastSavedVideo.getId());
    }

    @Test
    void likeVideoIncrementsLikeCountAndPublishesEvent() {
        VideoEntity video = video("video-1", "channel-1");
        ReflectionTestUtils.setField(video, "likeCount", 2L);
        videoRepository.videoById = Optional.of(video);

        VideoDto result = videoService.likeVideo("video-1");

        assertEquals(3L, result.getLikeCount());
        assertEquals(3L, ReflectionTestUtils.getField(video, "likeCount"));
        assertEquals("video-1", videoRepository.lastSavedVideo.getId());
    }

    private static class FakeVideoRepository {
        private List<VideoEntity> videosByChannel = List.of();
        private Optional<VideoEntity> videoById = Optional.empty();
        private String lastUploaderId;
        private String lastVisibility;
        private String lastFindById;
        private VideoEntity lastSavedVideo;

        private VideoRepository proxy() {
            return (VideoRepository) Proxy.newProxyInstance(
                    VideoRepository.class.getClassLoader(),
                    new Class<?>[]{VideoRepository.class},
                    (target, method, args) -> {
                        if (method.getName().equals("findByUploaderIdAndVisibilityOrderByUploadedAtDesc")) {
                            lastUploaderId = (String) args[0];
                            lastVisibility = (String) args[1];
                            return videosByChannel;
                        }
                        if (method.getName().equals("findById")) {
                            lastFindById = (String) args[0];
                            return videoById;
                        }
                        if (method.getName().equals("save")) {
                            lastSavedVideo = (VideoEntity) args[0];
                            return lastSavedVideo;
                        }
                        if (method.getName().equals("toString")) {
                            return "FakeVideoRepository";
                        }
                        throw new UnsupportedOperationException("Unsupported method: " + method.getName());
                    });
        }
    }

    private VideoEntity video(String id, String uploaderId) {
        LocalDateTime uploadedAt = LocalDateTime.of(2026, 6, 12, 10, 30);
        VideoEntity video = new VideoEntity.Builder()
                .setTitle("Design Patterns")
                .setDescription("Mini YouTube demo")
                .setThumbnail("https://example.com/thumb.jpg")
                .setVisibility("public")
                .setUploaderId(uploaderId)
                .setVideoUrl("https://example.com/video.mp4")
                .setDurationSeconds(180)
                .setCategory("Education")
                .build();

        ReflectionTestUtils.setField(video, "id", id);
        ReflectionTestUtils.setField(video, "viewCount", 12L);
        ReflectionTestUtils.setField(video, "likeCount", 3L);
        ReflectionTestUtils.setField(video, "uploadedAt", uploadedAt);
        return video;
    }

    private void assertVideoDto(VideoDto dto) {
        assertEquals("video-1", dto.getId());
        assertEquals("Design Patterns", dto.getTitle());
        assertEquals("Mini YouTube demo", dto.getDescription());
        assertEquals("channel-1", dto.getUploaderId());
        assertEquals("channel-1", dto.getChannelId());
        assertEquals("public", dto.getVisibility());
        assertEquals(12L, dto.getViewCount());
        assertEquals(3L, dto.getLikeCount());
        assertEquals(180, dto.getDuration());
        assertEquals("Education", dto.getCategory());
        assertEquals("https://example.com/thumb.jpg", dto.getThumbnailUrl());
        assertEquals("2026-06-12T10:30", dto.getCreatedAt());
    }
}
