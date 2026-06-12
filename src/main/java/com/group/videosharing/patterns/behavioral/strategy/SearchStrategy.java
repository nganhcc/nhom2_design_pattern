package com.group.videosharing.patterns.behavioral.strategy;

import com.group.videosharing.dto.VideoDto;
import java.util.List;

/** Strategy — Pattern 1 */
public interface SearchStrategy {
    List<VideoDto> execute(String query, List<VideoDto> items);
}
