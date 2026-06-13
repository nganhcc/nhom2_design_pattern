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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SearchServiceTest {

    private FakeVideoRepository videoRepository;
    private SearchService searchService;

    @BeforeEach
    void setUp() {
        videoRepository = new FakeVideoRepository();
        searchService = new SearchService(videoRepository.proxy(), new VideoMapper());
    }

    @Test
    void searchLoadsOnlyPublicVideos() {
        videoRepository.publicVideos = List.of(video("v1", "Public Video", "Education", 10, 100, "2026-06-10T10:00"));

        List<VideoDto> result = searchService.search("", "relevance", null, null, null, null, null);

        assertEquals("public", videoRepository.lastVisibility);
        assertEquals(List.of("v1"), result.stream().map(VideoDto::getId).toList());
    }

    @Test
    void searchSwitchesSortStrategy() {
        videoRepository.publicVideos = List.of(
                video("low", "Design Pattern Low", "Education", 1, 100, "2026-06-12T10:00"),
                video("high", "Design Pattern High", "Education", 30, 100, "2026-06-10T10:00"));

        List<VideoDto> result = searchService.search("design", "views", null, null, null, null, null);

        assertEquals(List.of("high", "low"), result.stream().map(VideoDto::getId).toList());
    }

    @Test
    void searchComposesCategoryDurationAndDateFilters() {
        videoRepository.publicVideos = List.of(
                video("match", "Builder Pattern", "Design Patterns", 10, 240, "2026-06-11T10:00"),
                video("wrong-category", "Builder Pattern", "Programming", 99, 240, "2026-06-11T10:00"),
                video("too-long", "Builder Pattern", "Design Patterns", 99, 700, "2026-06-11T10:00"),
                video("too-old", "Builder Pattern", "Design Patterns", 99, 240, "2026-06-09T10:00"));

        List<VideoDto> result = searchService.search(
                "builder",
                "relevance",
                "design patterns",
                100,
                300,
                "2026-06-10T00:00",
                "2026-06-12T00:00");

        assertEquals(List.of("match"), result.stream().map(VideoDto::getId).toList());
    }

    @Test
    void homeGuestReturnsPopularVideos() {
        videoRepository.publicVideos = List.of(
                video("low", "Low", "Education", 1, 100, "2026-06-10T10:00"),
                video("high", "High", "Programming", 40, 100, "2026-06-10T10:00"));

        List<VideoDto> result = searchService.home(null);

        assertEquals(List.of("high", "low"), result.stream().map(VideoDto::getId).toList());
    }

    @Test
    void homePersonalizedPrioritizesPopularCategoriesThenViews() {
        videoRepository.publicVideos = List.of(
                video("single-popular", "Single Popular", "Programming", 100, 100, "2026-06-10T10:00"),
                video("category-high", "Category High", "Education", 20, 100, "2026-06-10T10:00"),
                video("category-low", "Category Low", "Education", 1, 100, "2026-06-10T10:00"));

        List<VideoDto> result = searchService.home("viewer-1");

        assertEquals(List.of("category-high", "category-low", "single-popular"),
                result.stream().map(VideoDto::getId).toList());
    }

    @Test
    void searchRejectsInvalidSortAndDurationRange() {
        assertThrows(IllegalArgumentException.class,
                () -> searchService.search("", "unknown", null, null, null, null, null));
        assertThrows(IllegalArgumentException.class,
                () -> searchService.search("", "relevance", null, 500, 100, null, null));
    }

    private VideoEntity video(String id,
                              String title,
                              String category,
                              long viewCount,
                              int durationSeconds,
                              String uploadedAt) {
        VideoEntity video = new VideoEntity.Builder()
                .setTitle(title)
                .setDescription("Mini YouTube " + title)
                .setVisibility("public")
                .setUploaderId("channel-1")
                .setCategory(category)
                .setDurationSeconds(durationSeconds)
                .build();
        ReflectionTestUtils.setField(video, "id", id);
        ReflectionTestUtils.setField(video, "viewCount", viewCount);
        ReflectionTestUtils.setField(video, "uploadedAt", LocalDateTime.parse(uploadedAt));
        return video;
    }

    private static class FakeVideoRepository {
        private List<VideoEntity> publicVideos = List.of();
        private String lastVisibility;

        private VideoRepository proxy() {
            return (VideoRepository) Proxy.newProxyInstance(
                    VideoRepository.class.getClassLoader(),
                    new Class<?>[]{VideoRepository.class},
                    (target, method, args) -> {
                        if (method.getName().equals("findByVisibilityOrderByUploadedAtDesc")) {
                            lastVisibility = (String) args[0];
                            return publicVideos;
                        }
                        if (method.getName().equals("toString")) {
                            return "FakeVideoRepository";
                        }
                        throw new UnsupportedOperationException("Unsupported method: " + method.getName());
                    });
        }
    }
}
