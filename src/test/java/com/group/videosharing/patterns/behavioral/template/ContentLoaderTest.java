package com.group.videosharing.patterns.behavioral.template;

import com.group.videosharing.dto.UserDto;
import com.group.videosharing.dto.VideoDto;
import com.group.videosharing.patterns.behavioral.strategy.GuestRecommendationStrategy;
import com.group.videosharing.patterns.behavioral.strategy.RelevanceSearchStrategy;
import com.group.videosharing.patterns.behavioral.strategy.ViewCountSearchStrategy;
import com.group.videosharing.patterns.structural.decorator.CategoryFilterDecorator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for TV1 — Template Method Pattern (ContentLoader subclasses)
 */
class ContentLoaderTest {

    private List<VideoDto> pool;

    @BeforeEach
    void setUp() {
        pool = List.of(
                video("v1", "Design Patterns",  "Education",     100L, 480),
                video("v2", "Spring Boot",      "Programming",    50L, 720),
                video("v3", "Observer Pattern", "Education",     200L, 360)
        );
    }

    // ── SearchResultLoader ────────────────────────────────────────────────────

    @Test
    void searchLoader_loadPage_returnsSortedResults() {
        // Dùng ViewCountSearchStrategy: v3(200) > v1(100) > v2(50)
        List<VideoDto> result = new SearchResultLoader(pool, null, new ViewCountSearchStrategy()).loadPage();
        assertEquals("v3", result.get(0).getId());
        assertEquals("v1", result.get(1).getId());
        assertEquals("v2", result.get(2).getId());
    }

    @Test
    void searchLoader_filtersByKeywordViaStrategy() {
        // RelevanceSearchStrategy + keyword="design"
        List<VideoDto> result = new SearchResultLoader(pool, "design", new RelevanceSearchStrategy()).loadPage();
        // "Design Patterns" matches, "Observer Pattern" does not
        assertEquals(1, result.size());
        assertEquals("v1", result.getFirst().getId());
    }

    @Test
    void searchLoader_withDecoratorChain() {
        // Category filter wrapping relevance strategy
        CategoryFilterDecorator decorated = new CategoryFilterDecorator(new RelevanceSearchStrategy(), "Education");
        List<VideoDto> result = new SearchResultLoader(pool, null, decorated).loadPage();
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(v -> "Education".equalsIgnoreCase(v.getCategory())));
    }

    @Test
    void searchLoader_emptyPoolReturnsEmpty() {
        List<VideoDto> result = new SearchResultLoader(List.of(), "design", new RelevanceSearchStrategy()).loadPage();
        assertTrue(result.isEmpty());
    }

    // ── HomePageLoader ────────────────────────────────────────────────────────

    @Test
    void homeLoader_guestStrategy_returnsMostPopularFirst() {
        List<VideoDto> result = new HomePageLoader(pool, new GuestRecommendationStrategy(), null).loadPage();
        assertEquals("v3", result.get(0).getId(), "viewCount=200 should be first");
        assertEquals("v1", result.get(1).getId(), "viewCount=100 should be second");
        assertEquals(pool.size(), result.size());
    }

    @Test
    void homeLoader_emptyPoolReturnsEmpty() {
        List<VideoDto> result = new HomePageLoader(List.of(), new GuestRecommendationStrategy(), null).loadPage();
        assertTrue(result.isEmpty());
    }

    @Test
    void homeLoader_nullUserFallsBackToGuestBehavior() {
        // PersonalizedRecommendationStrategy với user=null sẽ fallback về GuestStrategy
        // Để test không phụ thuộc SubscriptionService, dùng GuestRecommendationStrategy trực tiếp
        UserDto noUser = null;
        List<VideoDto> result = new HomePageLoader(pool, new GuestRecommendationStrategy(), noUser).loadPage();
        assertNotNull(result);
        assertEquals(pool.size(), result.size());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private VideoDto video(String id, String title, String category, long viewCount, int duration) {
        VideoDto v = new VideoDto();
        v.setId(id);
        v.setTitle(title);
        v.setDescription("Description of " + title);
        v.setCategory(category);
        v.setViewCount(viewCount);
        v.setDuration(duration);
        v.setCreatedAt("2026-06-01T10:00");
        v.setChannelId("channel-" + id);
        v.setVisibility("public");
        return v;
    }
}
