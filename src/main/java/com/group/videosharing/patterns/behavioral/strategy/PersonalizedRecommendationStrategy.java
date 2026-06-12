package com.group.videosharing.patterns.behavioral.strategy;

import com.group.videosharing.dto.UserDto;
import com.group.videosharing.dto.VideoDto;
import java.util.List;

public class PersonalizedRecommendationStrategy implements RecommendationStrategy {
    @Override
    public List<VideoDto> recommend(UserDto user, List<VideoDto> pool) {
        // TODO: cá nhân hóa dựa trên lịch sử xem của user
        return pool;
    }
}
