package com.group.videosharing.patterns.structural.decorator;

import com.group.videosharing.dto.VideoDto;
import com.group.videosharing.patterns.behavioral.strategy.RelevanceSearchStrategy;
import com.group.videosharing.patterns.behavioral.strategy.SearchStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for TV1 — Decorator Pattern (CategoryFilter, DurationFilter, DateRangeFilter)
 */
class SearchDecoratorTest {

    private List<VideoDto> pool;

    @BeforeEach
    void setUp() {
        pool = List.of(
                video("v1", "Design Patterns",    "Education",       100L, 480, "2026-01-10T10:00"),
                video("v2", "Spring Boot",        "Programming",      50L, 720, "2026-03-05T09:00"),
                video("v3", "Observer Pattern",   "Design Patterns", 200L, 360, "2026-05-20T14:30"),
                video("v4", "JPA Guide",          "Programming",      10L, 600, "2026-02-01T08:00")
        );
    }

    // ── CategoryFilterDecorator ───────────────────────────────────────────────

    @Test
    void categoryFilter_keepsOnlyMatchingCategory() {
        SearchStrategy pipeline = new CategoryFilterDecorator(new RelevanceSearchStrategy(), "Programming");
        List<VideoDto> result = pipeline.execute(null, pool);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(v -> "Programming".equalsIgnoreCase(v.getCategory())));
    }

    @Test
    void categoryFilter_caseInsensitive() {
        SearchStrategy pipeline = new CategoryFilterDecorator(new RelevanceSearchStrategy(), "programming");
        List<VideoDto> result = pipeline.execute(null, pool);
        assertEquals(2, result.size());
    }

    @Test
    void categoryFilter_noMatchReturnsEmpty() {
        SearchStrategy pipeline = new CategoryFilterDecorator(new RelevanceSearchStrategy(), "Music");
        List<VideoDto> result = pipeline.execute(null, pool);
        assertTrue(result.isEmpty());
    }

    // ── DurationFilterDecorator ───────────────────────────────────────────────

    @Test
    void durationFilter_keepsVideosInRange() {
        SearchStrategy pipeline = new DurationFilterDecorator(new RelevanceSearchStrategy(), 400, 700);
        List<VideoDto> result = pipeline.execute(null, pool);
        // v1=480, v2=720 out, v3=360 out, v4=600 in => v1, v4
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(v -> v.getDuration() >= 400 && v.getDuration() <= 700));
    }

    @Test
    void durationFilter_exactBoundaryIncluded() {
        SearchStrategy pipeline = new DurationFilterDecorator(new RelevanceSearchStrategy(), 360, 480);
        List<VideoDto> result = pipeline.execute(null, pool);
        // v1=480, v3=360 => both included
        assertEquals(2, result.size());
    }

    // ── DateRangeFilterDecorator ──────────────────────────────────────────────

    @Test
    void dateFilter_keepsVideosInDateRange() {
        SearchStrategy pipeline = new DateRangeFilterDecorator(new RelevanceSearchStrategy(), "2026-02-01", "2026-04-01");
        List<VideoDto> result = pipeline.execute(null, pool);
        // v2=2026-03-05, v4=2026-02-01 => both in range
        assertEquals(2, result.size());
        assertTrue(result.stream().map(VideoDto::getId).toList().containsAll(List.of("v2", "v4")));
    }

    @Test
    void dateFilter_openFromBoundary() {
        // from=null → no lower bound
        SearchStrategy pipeline = new DateRangeFilterDecorator(new RelevanceSearchStrategy(), null, "2026-01-31");
        List<VideoDto> result = pipeline.execute(null, pool);
        // Only v1=2026-01-10 is before 2026-01-31
        assertEquals(1, result.size());
        assertEquals("v1", result.getFirst().getId());
    }

    @Test
    void dateFilter_openToBoundary() {
        // to=null → no upper bound
        SearchStrategy pipeline = new DateRangeFilterDecorator(new RelevanceSearchStrategy(), "2026-05-01", null);
        List<VideoDto> result = pipeline.execute(null, pool);
        // Only v3=2026-05-20
        assertEquals(1, result.size());
        assertEquals("v3", result.getFirst().getId());
    }

    @Test
    void dateFilter_bothNullReturnsAll() {
        SearchStrategy pipeline = new DateRangeFilterDecorator(new RelevanceSearchStrategy(), null, null);
        List<VideoDto> result = pipeline.execute(null, pool);
        assertEquals(pool.size(), result.size());
    }

    // ── Chained Decorators ────────────────────────────────────────────────────

    @Test
    void chainedDecorators_categoryAndDuration() {
        // Tìm video category=Programming VÀ duration <= 700
        SearchStrategy base     = new RelevanceSearchStrategy();
        SearchStrategy withCat  = new CategoryFilterDecorator(base, "Programming");
        SearchStrategy withDur  = new DurationFilterDecorator(withCat, 0, 700);
        List<VideoDto> result = withDur.execute(null, pool);
        // v2=720 filtered by duration; v4=600 passes → only v4
        assertEquals(1, result.size());
        assertEquals("v4", result.getFirst().getId());
    }

    @Test
    void chainedDecorators_categoryAndKeyword() {
        SearchStrategy pipeline = new CategoryFilterDecorator(new RelevanceSearchStrategy(), "Design Patterns");
        List<VideoDto> result = pipeline.execute("observer", pool);
        assertEquals(1, result.size());
        assertEquals("v3", result.getFirst().getId());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private VideoDto video(String id, String title, String category,
                           long viewCount, int duration, String createdAt) {
        VideoDto v = new VideoDto();
        v.setId(id);
        v.setTitle(title);
        v.setDescription("Description of " + title);
        v.setCategory(category);
        v.setViewCount(viewCount);
        v.setDuration(duration);
        v.setCreatedAt(createdAt);
        v.setChannelId("channel-" + id);
        v.setVisibility("public");
        return v;
    }
}
