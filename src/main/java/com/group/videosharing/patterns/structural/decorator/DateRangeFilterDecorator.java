package com.group.videosharing.patterns.structural.decorator;

import com.group.videosharing.dto.VideoDto;
import com.group.videosharing.patterns.behavioral.strategy.SearchStrategy;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class DateRangeFilterDecorator extends SearchResultDecorator {
    private final String from;
    private final String to;

    public DateRangeFilterDecorator(SearchStrategy wrapped, String from, String to) {
        super(wrapped);
        this.from = from;
        this.to   = to;
    }

    @Override
    public List<VideoDto> execute(String query, List<VideoDto> items) {
        LocalDateTime fromDate = parseBoundary(from, "from");
        LocalDateTime toDate = parseBoundary(to, "to");
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("from must be before or equal to to");
        }

        return wrapped.execute(query, items).stream()
                .filter(video -> inRange(video.getCreatedAt(), fromDate, toDate))
                .toList();
    }

    private boolean inRange(String createdAt, LocalDateTime fromDate, LocalDateTime toDate) {
        LocalDateTime createdDate = parseRequiredCreatedAt(createdAt);
        boolean afterFrom = fromDate == null || !createdDate.isBefore(fromDate);
        boolean beforeTo = toDate == null || !createdDate.isAfter(toDate);
        return afterFrom && beforeTo;
    }

    private LocalDateTime parseBoundary(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value.trim());
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(fieldName + " must be ISO date-time");
        }
    }

    private LocalDateTime parseRequiredCreatedAt(String value) {
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException | NullPointerException ex) {
            throw new IllegalArgumentException("video createdAt must be ISO date-time");
        }
    }
}
