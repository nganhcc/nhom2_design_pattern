package com.group.videosharing.patterns.structural.decorator;

import com.group.videosharing.dto.VideoDto;
import com.group.videosharing.patterns.behavioral.strategy.SearchStrategy;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Decorator lọc video theo khoảng ngày upload.
 * from/to nhận dạng "yyyy-MM-dd"; null = không giới hạn đầu/cuối.
 */
public class DateRangeFilterDecorator extends SearchResultDecorator {
    private static final DateTimeFormatter DATE_FMT      = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT  = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private final LocalDate from;
    private final LocalDate to;

    public DateRangeFilterDecorator(SearchStrategy wrapped, String from, String to) {
        super(wrapped);
        this.from = parseDate(from);
        this.to   = parseDate(to);
    }

    @Override
    public List<VideoDto> execute(String query, List<VideoDto> items) {
        List<VideoDto> results = wrapped.execute(query, items);
        if (from == null && to == null) return results;
        return results.stream()
                .filter(v -> inRange(v.getCreatedAt()))
                .toList();
    }

    private boolean inRange(String createdAt) {
        if (createdAt == null) return false;
        try {
            LocalDate date = LocalDateTime.parse(createdAt, DATETIME_FMT).toLocalDate();
            if (from != null && date.isBefore(from)) return false;
            if (to   != null && date.isAfter(to))   return false;
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return LocalDate.parse(s, DATE_FMT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
