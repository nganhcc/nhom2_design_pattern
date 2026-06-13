package com.group.videosharing.patterns.behavioral.strategy;

import com.group.videosharing.dto.VideoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for TV1 — Strategy Pattern (SearchStrategy + RecommendationStrategy)
 */
class SearchStrategyTest {

    private List<VideoDto> pool;

    @BeforeEach
    void setUp() {
        pool = List.of(
                video("v1", "Design Patterns Overview",    "Intro to patterns",        "Education",  100L, 480, "2026-01-10T10:00"),
                video("v2", "Spring Boot Tutorial",        "Build a REST API",          "Programming", 50L, 720, "2026-03-05T09:00"),
                video("v3", "Observer Pattern in Action",  "EventBus demo with design", "Design Patterns", 200L, 360, "2026-05-20T14:30"),
                video("v4", "JPA Repository Guide",        "Data access layer",         "Programming", 10L, 600, "2026-02-01T08:00")
        );
    }

    // ── RelevanceSearchStrategy ───────────────────────────────────────────────

    @Test
    void relevance_filtersByKeywordInTitle() {
        List<VideoDto> result = new RelevanceSearchStrategy().execute("spring", pool);
        assertEquals(1, result.size());
        assertEquals("v2", result.getFirst().getId());
    }

    @Test
    void relevance_filtersByKeywordInDescription() {
        List<VideoDto> result = new RelevanceSearchStrategy().execute("eventbus", pool);
        assertEquals(1, result.size());
        assertEquals("v3", result.getFirst().getId());
    }

    @Test
    void relevance_titleMatchesRankedFirst() {
        // "design" xuất hiện ở title v1 và description v3
        List<VideoDto> result = new RelevanceSearchStrategy().execute("design", pool);
        assertEquals(2, result.size());
        assertEquals("v1", result.get(0).getId(), "Title match should come first");
        assertEquals("v3", result.get(1).getId(), "Description match should come second");
    }

    @Test
    void relevance_blankQueryReturnsAll() {
        List<VideoDto> result = new RelevanceSearchStrategy().execute("", pool);
        assertEquals(pool.size(), result.size());
    }

    @Test
    void relevance_nullQueryReturnsAll() {
        List<VideoDto> result = new RelevanceSearchStrategy().execute(null, pool);
        assertEquals(pool.size(), result.size());
    }

    @Test
    void relevance_noMatchReturnsEmpty() {
        List<VideoDto> result = new RelevanceSearchStrategy().execute("nonexistentkeyword", pool);
        assertTrue(result.isEmpty());
    }

    // ── DateSearchStrategy ────────────────────────────────────────────────────

    @Test
    void date_sortsByCreatedAtDescending() {
        List<VideoDto> result = new DateSearchStrategy().execute(null, pool);
        assertEquals("v3", result.get(0).getId(), "2026-05-20 should be first");
        assertEquals("v2", result.get(1).getId(), "2026-03-05 should be second");
    }

    @Test
    void date_filtersByKeywordAndSortsByDate() {
        List<VideoDto> result = new DateSearchStrategy().execute("pattern", pool);
        // v1 "Design Patterns Overview" and v3 "Observer Pattern in Action" match
        assertEquals(2, result.size());
        assertEquals("v3", result.get(0).getId(), "Newer match first");
    }

    // ── ViewCountSearchStrategy ───────────────────────────────────────────────

    @Test
    void viewCount_sortsByViewCountDescending() {
        List<VideoDto> result = new ViewCountSearchStrategy().execute(null, pool);
        assertEquals("v3", result.get(0).getId(), "viewCount=200 first");
        assertEquals("v1", result.get(1).getId(), "viewCount=100 second");
    }

    @Test
    void viewCount_filtersByKeywordAndSortsByViews() {
        List<VideoDto> result = new ViewCountSearchStrategy().execute("pattern", pool);
        assertEquals(2, result.size());
        assertEquals("v3", result.get(0).getId(), "viewCount=200 first");
    }

    // ── GuestRecommendationStrategy ───────────────────────────────────────────

    @Test
    void guest_returnsMostPopularFirst() {
        List<VideoDto> result = new GuestRecommendationStrategy().recommend(null, pool);
        assertEquals("v3", result.get(0).getId(), "viewCount=200 should be first");
        assertEquals("v1", result.get(1).getId(), "viewCount=100 should be second");
        assertEquals(pool.size(), result.size());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private VideoDto video(String id, String title, String description, String category,
                           long viewCount, int duration, String createdAt) {
        VideoDto v = new VideoDto();
        v.setId(id);
        v.setTitle(title);
        v.setDescription(description);
        v.setCategory(category);
        v.setViewCount(viewCount);
        v.setDuration(duration);
        v.setCreatedAt(createdAt);
        v.setChannelId("channel-" + id);
        v.setUploaderId("channel-" + id);
        v.setVisibility("public");
        return v;
    }
}
