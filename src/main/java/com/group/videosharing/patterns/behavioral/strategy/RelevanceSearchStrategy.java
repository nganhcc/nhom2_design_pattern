package com.group.videosharing.patterns.behavioral.strategy;

import com.group.videosharing.dto.VideoDto;
import java.util.List;

public class RelevanceSearchStrategy implements SearchStrategy {
    @Override
    public List<VideoDto> execute(String query, List<VideoDto> items) {
        // TODO: sort theo độ liên quan với query
        return items;
    }
}
