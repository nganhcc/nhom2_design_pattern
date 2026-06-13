package com.group.videosharing.patterns.behavioral.strategy;

import com.group.videosharing.dto.VideoDto;
import java.util.Comparator;
import java.util.List;

public class ViewCountSearchStrategy implements SearchStrategy {
    @Override
    public List<VideoDto> execute(String query, List<VideoDto> items) {
        List<VideoDto> filtered = (query == null || query.isBlank())
                ? items
                : items.stream().filter(v -> matchesKeyword(v, query.toLowerCase())).toList();
        return filtered.stream()
                .sorted(Comparator.comparingLong(VideoDto::getViewCount).reversed())
                .toList();
    }

    private boolean matchesKeyword(VideoDto v, String q) {
        String title = v.getTitle() == null ? "" : v.getTitle().toLowerCase();
        String desc  = v.getDescription() == null ? "" : v.getDescription().toLowerCase();
        return title.contains(q) || desc.contains(q);
    }
}
