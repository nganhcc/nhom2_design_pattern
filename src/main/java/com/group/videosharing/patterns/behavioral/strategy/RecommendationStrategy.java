package com.group.videosharing.patterns.behavioral.strategy;

import com.group.videosharing.dto.UserDto;
import com.group.videosharing.dto.VideoDto;
import java.util.List;

public interface RecommendationStrategy {
    List<VideoDto> recommend(UserDto user, List<VideoDto> pool);
}
