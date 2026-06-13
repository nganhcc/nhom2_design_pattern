package com.group.videosharing.patterns.behavioral.strategy;

import com.group.videosharing.dto.VideoDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchStrategyTest {

    @Test
    void relevanceSortsByTitleDescriptionThenCategoryMatch() {
        VideoDto categoryMatch = video("category", "Clean Architecture", "No keyword here", "Observer", 50, "2026-06-10T10:00");
        VideoDto descriptionMatch = video("description", "Architecture Notes", "Observer keyword in the description", "Programming", 10, "2026-06-11T10:00");
        VideoDto titleMatch = video("title", "Observer Pattern", "Backend eventing", "Programming", 1, "2026-06-09T10:00");

        List<VideoDto> result = new RelevanceSearchStrategy()
                .execute("observer", List.of(categoryMatch, descriptionMatch, titleMatch));

        assertEquals(List.of("title", "description", "category"), result.stream().map(VideoDto::getId).toList());
    }

    @Test
    void dateSortsNewestFirst() {
        List<VideoDto> result = new DateSearchStrategy().execute("", List.of(
                video("old", "Old", "", "Education", 1, "2026-06-10T10:00"),
                video("new", "New", "", "Education", 1, "2026-06-12T10:00"),
                video("middle", "Middle", "", "Education", 1, "2026-06-11T10:00")));

        assertEquals(List.of("new", "middle", "old"), result.stream().map(VideoDto::getId).toList());
    }

    @Test
    void viewsSortsHighestViewCountFirst() {
        List<VideoDto> result = new ViewCountSearchStrategy().execute("", List.of(
                video("low", "Low", "", "Education", 1, "2026-06-10T10:00"),
                video("high", "High", "", "Education", 20, "2026-06-10T10:00"),
                video("medium", "Medium", "", "Education", 10, "2026-06-10T10:00")));

        assertEquals(List.of("high", "medium", "low"), result.stream().map(VideoDto::getId).toList());
    }

    private VideoDto video(String id,
                           String title,
                           String description,
                           String category,
                           long viewCount,
                           String createdAt) {
        VideoDto video = new VideoDto();
        video.setId(id);
        video.setTitle(title);
        video.setDescription(description);
        video.setCategory(category);
        video.setViewCount(viewCount);
        video.setCreatedAt(createdAt);
        return video;
    }
}
