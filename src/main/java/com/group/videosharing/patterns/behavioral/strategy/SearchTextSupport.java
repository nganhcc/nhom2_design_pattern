package com.group.videosharing.patterns.behavioral.strategy;

import com.group.videosharing.dto.VideoDto;

final class SearchTextSupport {

    private SearchTextSupport() {
    }

    static boolean matches(String query, VideoDto video) {
        return relevanceScore(query, video) > 0 || normalized(query).isBlank();
    }

    static int relevanceScore(String query, VideoDto video) {
        String normalizedQuery = normalized(query);
        if (normalizedQuery.isBlank()) {
            return 1;
        }

        int score = 0;
        if (contains(video.getTitle(), normalizedQuery)) {
            score += 3;
        }
        if (contains(video.getDescription(), normalizedQuery)) {
            score += 2;
        }
        if (contains(video.getCategory(), normalizedQuery)) {
            score += 1;
        }
        return score;
    }

    private static boolean contains(String value, String query) {
        return value != null && value.toLowerCase().contains(query);
    }

    private static String normalized(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
