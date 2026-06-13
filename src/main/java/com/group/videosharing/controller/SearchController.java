package com.group.videosharing.controller;

import com.group.videosharing.dto.UserDto;
import com.group.videosharing.dto.VideoDto;
import com.group.videosharing.patterns.behavioral.strategy.*;
import com.group.videosharing.patterns.behavioral.template.HomePageLoader;
import com.group.videosharing.patterns.behavioral.template.SearchResultLoader;
import com.group.videosharing.patterns.creational.singleton.SessionManager;
import com.group.videosharing.patterns.structural.decorator.CategoryFilterDecorator;
import com.group.videosharing.patterns.structural.decorator.DateRangeFilterDecorator;
import com.group.videosharing.patterns.structural.decorator.DurationFilterDecorator;
import com.group.videosharing.patterns.structural.facade.IVideoService;
import com.group.videosharing.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TV1 — SearchController
 * Kết hợp: Strategy (sort/recommend) + Decorator (filters) + Template Method (ContentLoader)
 */
@RestController
public class SearchController {

    private final IVideoService       videoService;
    private final SubscriptionService subscriptionService;

    public SearchController(IVideoService videoService,
                            SubscriptionService subscriptionService) {
        this.videoService       = videoService;
        this.subscriptionService = subscriptionService;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/search
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Tìm kiếm và lọc video.
     *
     * @param q           Từ khóa (nullable)
     * @param sort        Thuật toán: "relevance" (default) | "date" | "views"
     * @param category    Lọc theo thể loại (nullable)
     * @param minDuration Thời lượng tối thiểu tính bằng giây (nullable)
     * @param maxDuration Thời lượng tối đa tính bằng giây (nullable)
     * @param from        Ngày bắt đầu "yyyy-MM-dd" (nullable)
     * @param to          Ngày kết thúc "yyyy-MM-dd" (nullable)
     */
    @GetMapping("/api/search")
    public ResponseEntity<List<VideoDto>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false, defaultValue = "relevance") String sort,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer minDuration,
            @RequestParam(required = false) Integer maxDuration,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        // 1. Chọn Strategy theo sort param (Pattern: Strategy)
        SearchStrategy strategy = selectSearchStrategy(sort);

        // 2. Wrap Strategy bằng Decorator chain theo thứ tự (Pattern: Decorator)
        if (category != null && !category.isBlank()) {
            strategy = new CategoryFilterDecorator(strategy, category);
        }
        if (minDuration != null || maxDuration != null) {
            int min = minDuration != null ? minDuration : 0;
            int max = maxDuration != null ? maxDuration : Integer.MAX_VALUE;
            strategy = new DurationFilterDecorator(strategy, min, max);
        }
        if (from != null || to != null) {
            strategy = new DateRangeFilterDecorator(strategy, from, to);
        }

        // 3. Tải toàn bộ video public từ VideoService
        List<VideoDto> allVideos = videoService.getAllPublicVideos();

        // 4. Chạy Template Method: fetchData → processData → sortData (= strategy pipeline)
        List<VideoDto> results = new SearchResultLoader(allVideos, q, strategy).loadPage();

        return ResponseEntity.ok(results);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/home
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Trang chủ / đề xuất video.
     *
     * @param viewerId ID người dùng (nullable — nếu không cung cấp sẽ dùng session hiện tại)
     */
    @GetMapping("/api/home")
    public ResponseEntity<List<VideoDto>> home(
            @RequestParam(required = false) String viewerId) {

        // Xác định user hiện tại (Pattern: Singleton — SessionManager)
        UserDto currentUser = SessionManager.getInstance().getCurrentUser();

        // 1. Chọn RecommendationStrategy (Pattern: Strategy)
        RecommendationStrategy recommendationStrategy;
        if (currentUser != null) {
            recommendationStrategy = new PersonalizedRecommendationStrategy(subscriptionService);
        } else {
            recommendationStrategy = new GuestRecommendationStrategy();
        }

        // 2. Tải toàn bộ video public
        List<VideoDto> allVideos = videoService.getAllPublicVideos();

        // 3. Chạy Template Method: fetchData → processData (= recommend) → sortData
        List<VideoDto> results = new HomePageLoader(allVideos, recommendationStrategy, currentUser).loadPage();

        return ResponseEntity.ok(results);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    /** Chọn SearchStrategy dựa trên sort param */
    private SearchStrategy selectSearchStrategy(String sort) {
        if (sort == null) return new RelevanceSearchStrategy();
        return switch (sort.toLowerCase()) {
            case "date"  -> new DateSearchStrategy();
            case "views" -> new ViewCountSearchStrategy();
            default      -> new RelevanceSearchStrategy();
        };
    }
}
