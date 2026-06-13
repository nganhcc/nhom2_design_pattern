package com.group.videosharing.patterns.behavioral.strategy;

import com.group.videosharing.dto.UserDto;
import com.group.videosharing.dto.VideoDto;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PersonalizedRecommendationStrategy implements RecommendationStrategy {
    @Override
    public List<VideoDto> recommend(UserDto user, List<VideoDto> pool) {
        Map<String, Long> categoryPopularity = pool.stream()
                .map(VideoDto::getCategory)
                .filter(category -> category != null && !category.isBlank())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return pool.stream()
                .sorted(Comparator
                        .comparing((VideoDto video) -> categoryPopularity.getOrDefault(video.getCategory(), 0L),
                                Comparator.reverseOrder())
                        .thenComparing(VideoDto::getViewCount, Comparator.reverseOrder())
                        .thenComparing(VideoDto::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(VideoDto::getId, Comparator.nullsLast(String::compareTo)))
                .toList();
    }
}
