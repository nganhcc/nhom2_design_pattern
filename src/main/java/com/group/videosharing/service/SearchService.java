package com.group.videosharing.service;

import com.group.videosharing.dto.UserDto;
import com.group.videosharing.dto.VideoDto;
import com.group.videosharing.patterns.behavioral.strategy.DateSearchStrategy;
import com.group.videosharing.patterns.behavioral.strategy.GuestRecommendationStrategy;
import com.group.videosharing.patterns.behavioral.strategy.PersonalizedRecommendationStrategy;
import com.group.videosharing.patterns.behavioral.strategy.RecommendationStrategy;
import com.group.videosharing.patterns.behavioral.strategy.RelevanceSearchStrategy;
import com.group.videosharing.patterns.behavioral.strategy.SearchContext;
import com.group.videosharing.patterns.behavioral.strategy.SearchStrategy;
import com.group.videosharing.patterns.behavioral.strategy.ViewCountSearchStrategy;
import com.group.videosharing.patterns.behavioral.template.HomePageLoader;
import com.group.videosharing.patterns.behavioral.template.SearchResultLoader;
import com.group.videosharing.patterns.structural.decorator.CategoryFilterDecorator;
import com.group.videosharing.patterns.structural.decorator.DateRangeFilterDecorator;
import com.group.videosharing.patterns.structural.decorator.DurationFilterDecorator;
import com.group.videosharing.repository.VideoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    private static final String PUBLIC_VISIBILITY = "public";

    private final VideoRepository videoRepository;
    private final VideoMapper videoMapper;

    public SearchService(VideoRepository videoRepository, VideoMapper videoMapper) {
        this.videoRepository = videoRepository;
        this.videoMapper = videoMapper;
    }

    public List<VideoDto> search(String query,
                                 String sort,
                                 String category,
                                 Integer minDuration,
                                 Integer maxDuration,
                                 String from,
                                 String to) {
        validateDurationRange(minDuration, maxDuration);

        SearchStrategy strategy = buildSearchStrategy(sort);
        strategy = decorate(strategy, category, minDuration, maxDuration, from, to);

        SearchResultLoader loader = new SearchResultLoader(
                query,
                this::loadPublicVideos,
                new SearchContext(strategy));
        return loader.loadPage();
    }

    public List<VideoDto> home(String viewerId) {
        RecommendationStrategy strategy = isBlank(viewerId)
                ? new GuestRecommendationStrategy()
                : new PersonalizedRecommendationStrategy();
        UserDto viewer = isBlank(viewerId) ? null : viewer(viewerId);

        HomePageLoader loader = new HomePageLoader(this::loadPublicVideos, strategy, viewer);
        return loader.loadPage();
    }

    private List<VideoDto> loadPublicVideos() {
        return videoRepository.findByVisibilityOrderByUploadedAtDesc(PUBLIC_VISIBILITY)
                .stream()
                .map(videoMapper::toDto)
                .toList();
    }

    private SearchStrategy buildSearchStrategy(String sort) {
        String normalizedSort = isBlank(sort) ? "relevance" : sort.trim().toLowerCase();
        return switch (normalizedSort) {
            case "relevance" -> new RelevanceSearchStrategy();
            case "date" -> new DateSearchStrategy();
            case "views" -> new ViewCountSearchStrategy();
            default -> throw new IllegalArgumentException("sort must be one of: relevance, date, views");
        };
    }

    private SearchStrategy decorate(SearchStrategy strategy,
                                    String category,
                                    Integer minDuration,
                                    Integer maxDuration,
                                    String from,
                                    String to) {
        SearchStrategy decorated = strategy;
        if (!isBlank(category)) {
            decorated = new CategoryFilterDecorator(decorated, category.trim());
        }
        if (minDuration != null || maxDuration != null) {
            int min = minDuration != null ? minDuration : 0;
            int max = maxDuration != null ? maxDuration : Integer.MAX_VALUE;
            decorated = new DurationFilterDecorator(decorated, min, max);
        }
        if (!isBlank(from) || !isBlank(to)) {
            decorated = new DateRangeFilterDecorator(decorated, from, to);
        }
        return decorated;
    }

    private void validateDurationRange(Integer minDuration, Integer maxDuration) {
        if ((minDuration != null && minDuration < 0) || (maxDuration != null && maxDuration < 0)) {
            throw new IllegalArgumentException("duration must not be negative");
        }
        if (minDuration != null && maxDuration != null && minDuration > maxDuration) {
            throw new IllegalArgumentException("minDuration must be less than or equal to maxDuration");
        }
    }

    private UserDto viewer(String viewerId) {
        UserDto user = new UserDto();
        user.setId(viewerId.trim());
        user.setChannelId(viewerId.trim());
        return user;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
