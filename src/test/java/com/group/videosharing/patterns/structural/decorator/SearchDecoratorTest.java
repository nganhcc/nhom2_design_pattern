package com.group.videosharing.patterns.structural.decorator;

import com.group.videosharing.dto.VideoDto;
import com.group.videosharing.patterns.behavioral.strategy.RelevanceSearchStrategy;
import com.group.videosharing.patterns.behavioral.strategy.SearchStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SearchDecoratorTest {

    @Test
    void categoryFilterIsCaseInsensitive() {
        SearchStrategy strategy = new CategoryFilterDecorator(new RelevanceSearchStrategy(), "education");

        List<VideoDto> result = strategy.execute("", List.of(
                videoWithCategory("education", "Education"),
                videoWithCategory("programming", "Programming")));

        assertEquals(List.of("education"), result.stream().map(VideoDto::getId).toList());
    }

    @Test
    void durationFilterKeepsVideosInsideRange() {
        SearchStrategy strategy = new DurationFilterDecorator(new RelevanceSearchStrategy(), 100, 300);

        List<VideoDto> result = strategy.execute("", List.of(
                video("short", 90),
                video("inside", 200),
                video("long", 301)));

        assertEquals(List.of("inside"), result.stream().map(VideoDto::getId).toList());
    }

    @Test
    void dateRangeFilterIsInclusive() {
        SearchStrategy strategy = new DateRangeFilterDecorator(
                new RelevanceSearchStrategy(),
                "2026-06-10T00:00",
                "2026-06-12T00:00");

        List<VideoDto> result = strategy.execute("", List.of(
                video("before", "2026-06-09T23:59"),
                video("from", "2026-06-10T00:00"),
                video("to", "2026-06-12T00:00"),
                video("after", "2026-06-12T00:01")));

        assertEquals(List.of("to", "from"), result.stream().map(VideoDto::getId).toList());
    }

    @Test
    void dateRangeFilterRejectsInvalidDateAndRange() {
        SearchStrategy invalidDate = new DateRangeFilterDecorator(new RelevanceSearchStrategy(), "not-a-date", null);
        SearchStrategy invalidRange = new DateRangeFilterDecorator(
                new RelevanceSearchStrategy(),
                "2026-06-12T00:00",
                "2026-06-10T00:00");

        assertThrows(IllegalArgumentException.class, () -> invalidDate.execute("", List.of(video("v1", "2026-06-10T00:00"))));
        assertThrows(IllegalArgumentException.class, () -> invalidRange.execute("", List.of(video("v1", "2026-06-10T00:00"))));
    }

    private VideoDto videoWithCategory(String id, String category) {
        VideoDto video = video(id, "2026-06-10T00:00");
        video.setCategory(category);
        return video;
    }

    private VideoDto video(String id, int duration) {
        VideoDto video = video(id, "2026-06-10T00:00");
        video.setDuration(duration);
        return video;
    }

    private VideoDto video(String id, String createdAt) {
        VideoDto video = new VideoDto();
        video.setId(id);
        video.setTitle("Design Patterns");
        video.setCategory("Education");
        video.setCreatedAt(createdAt);
        return video;
    }
}
