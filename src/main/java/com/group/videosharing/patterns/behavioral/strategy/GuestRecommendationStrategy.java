package com.group.videosharing.patterns.behavioral.strategy;

import com.group.videosharing.dto.UserDto;
import com.group.videosharing.dto.VideoDto;
import java.util.Comparator;
import java.util.List;

public class GuestRecommendationStrategy implements RecommendationStrategy {
    @Override
    public List<VideoDto> recommend(UserDto user, List<VideoDto> pool) {
        // Guest — không có lịch sử, trả về video phổ biến nhất (viewCount cao)
        return pool.stream()
                .sorted(Comparator.comparingLong(VideoDto::getViewCount).reversed())
                .toList();
    }
}
