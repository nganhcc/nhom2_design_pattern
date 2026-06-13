package com.group.videosharing.controller;

import com.group.videosharing.domain.VideoEntity;
import com.group.videosharing.dto.VideoDto;
import com.group.videosharing.repository.VideoRepository;
import com.group.videosharing.service.SearchService;
import com.group.videosharing.service.VideoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchControllerTest {

    private FakeVideoRepository videoRepository;
    private SearchController searchController;

    @BeforeEach
    void setUp() {
        videoRepository = new FakeVideoRepository();
        SearchService searchService = new SearchService(videoRepository.proxy(), new VideoMapper());
        searchController = new SearchController(searchService);
    }

    @Test
    void searchReturnsOkWithResults() {
        videoRepository.publicVideos = List.of(video("video-1", "Design Patterns", "Education", 12));

        ResponseEntity<?> response = searchController.search(
                "design", "relevance", null, null, null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<?> body = (List<?>) response.getBody();
        VideoDto first = (VideoDto) body.getFirst();
        assertEquals("video-1", first.getId());
    }

    @Test
    void searchReturnsBadRequestForInvalidSort() {
        ResponseEntity<?> response = searchController.search(
                "", "bad-sort", null, null, null, null, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("sort must be one of: relevance, date, views", response.getBody());
    }

    @Test
    void homeReturnsOkWithRecommendations() {
        videoRepository.publicVideos = List.of(
                video("low", "Low", "Education", 1),
                video("high", "High", "Programming", 20));

        ResponseEntity<?> response = searchController.home(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<?> body = (List<?>) response.getBody();
        VideoDto first = (VideoDto) body.getFirst();
        assertEquals("high", first.getId());
    }

    private VideoEntity video(String id, String title, String category, long viewCount) {
        VideoEntity video = new VideoEntity.Builder()
                .setTitle(title)
                .setDescription("Search controller demo")
                .setVisibility("public")
                .setUploaderId("channel-1")
                .setCategory(category)
                .setDurationSeconds(120)
                .build();
        ReflectionTestUtils.setField(video, "id", id);
        ReflectionTestUtils.setField(video, "viewCount", viewCount);
        ReflectionTestUtils.setField(video, "uploadedAt", LocalDateTime.of(2026, 6, 12, 10, 0));
        return video;
    }

    private static class FakeVideoRepository {
        private List<VideoEntity> publicVideos = List.of();

        private VideoRepository proxy() {
            return (VideoRepository) Proxy.newProxyInstance(
                    VideoRepository.class.getClassLoader(),
                    new Class<?>[]{VideoRepository.class},
                    (target, method, args) -> {
                        if (method.getName().equals("findByVisibilityOrderByUploadedAtDesc")) {
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
