package com.group.videosharing.patterns.structural.decorator;

import com.group.videosharing.dto.VideoDto;
import com.group.videosharing.patterns.behavioral.strategy.SearchStrategy;
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
        // TODO: filter theo createdAt trong khoảng [from, to]
        return wrapped.execute(query, items);
    }
}
