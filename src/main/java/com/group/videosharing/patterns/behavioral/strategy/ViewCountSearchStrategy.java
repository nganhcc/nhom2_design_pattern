package com.group.videosharing.patterns.behavioral.strategy;

import com.group.videosharing.dto.VideoDto;
import java.util.List;

public class ViewCountSearchStrategy implements SearchStrategy {
    @Override
    public List<VideoDto> execute(String query, List<VideoDto> items) {
        // TODO: sort theo viewCount giảm dần
        return items;
    }
}
