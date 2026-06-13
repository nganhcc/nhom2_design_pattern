package com.group.videosharing.patterns.behavioral.strategy;

import com.group.videosharing.dto.VideoDto;
import java.util.Comparator;
import java.util.List;

public class RelevanceSearchStrategy implements SearchStrategy {
    @Override
    public List<VideoDto> execute(String query, List<VideoDto> items) {
        if (query == null || query.isBlank()) {
            return items;
        }
        String q = query.toLowerCase();
        return items.stream()
                .filter(v -> matchesKeyword(v, q))
                .sorted(Comparator.comparingInt(v -> titleScore(v, q)))
                .toList();
    }

    private boolean matchesKeyword(VideoDto v, String q) {
        String title = v.getTitle() == null ? "" : v.getTitle().toLowerCase();
        String desc  = v.getDescription() == null ? "" : v.getDescription().toLowerCase();
        return title.contains(q) || desc.contains(q);
    }

    /** 0 = keyword nằm trong title (ưu tiên cao hơn), 1 = chỉ nằm trong description */
    private int titleScore(VideoDto v, String q) {
        String title = v.getTitle() == null ? "" : v.getTitle().toLowerCase();
        return title.contains(q) ? 0 : 1;
    }
}
