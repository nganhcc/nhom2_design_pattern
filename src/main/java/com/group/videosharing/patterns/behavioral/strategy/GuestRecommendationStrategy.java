package com.group.videosharing.patterns.behavioral.strategy;

import com.group.videosharing.dto.UserDto;
import com.group.videosharing.dto.VideoDto;
import java.util.List;

public class GuestRecommendationStrategy implements RecommendationStrategy {
    @Override
    public List<VideoDto> recommend(UserDto user, List<VideoDto> pool) {
        // TODO: trả về nội dung phổ biến (viewCount cao)
        return pool;
    }
}
