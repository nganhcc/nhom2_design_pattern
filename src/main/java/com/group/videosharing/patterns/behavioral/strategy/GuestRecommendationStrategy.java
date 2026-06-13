package com.group.videosharing.patterns.behavioral.strategy;

import com.group.videosharing.dto.UserDto;
import com.group.videosharing.dto.VideoDto;
import java.util.Comparator;
import java.util.List;

public class GuestRecommendationStrategy implements RecommendationStrategy {
    @Override
    public List<VideoDto> recommend(UserDto user, List<VideoDto> pool) {
        return pool.stream()
                .sorted(Comparator
                        .comparing(VideoDto::getViewCount, Comparator.reverseOrder())
                        .thenComparing(VideoDto::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(VideoDto::getId, Comparator.nullsLast(String::compareTo)))
                .toList();
    }
}
