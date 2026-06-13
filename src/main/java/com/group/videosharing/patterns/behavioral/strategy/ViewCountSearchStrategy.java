package com.group.videosharing.patterns.behavioral.strategy;

import com.group.videosharing.dto.VideoDto;
import java.util.Comparator;
import java.util.List;

public class ViewCountSearchStrategy implements SearchStrategy {
    @Override
    public List<VideoDto> execute(String query, List<VideoDto> items) {
        return items.stream()
                .filter(video -> SearchTextSupport.matches(query, video))
                .sorted(Comparator
                        .comparing(VideoDto::getViewCount, Comparator.reverseOrder())
                        .thenComparing(VideoDto::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(VideoDto::getId, Comparator.nullsLast(String::compareTo)))
                .toList();
    }
}
