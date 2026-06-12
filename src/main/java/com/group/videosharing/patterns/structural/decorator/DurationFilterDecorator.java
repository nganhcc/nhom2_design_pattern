package com.group.videosharing.patterns.structural.decorator;

import com.group.videosharing.dto.VideoDto;
import com.group.videosharing.patterns.behavioral.strategy.SearchStrategy;
import java.util.List;
import java.util.stream.Collectors;

public class DurationFilterDecorator extends SearchResultDecorator {
    private final int minSec;
    private final int maxSec;

    public DurationFilterDecorator(SearchStrategy wrapped, int minSec, int maxSec) {
        super(wrapped);
        this.minSec = minSec;
        this.maxSec = maxSec;
    }

    @Override
    public List<VideoDto> execute(String query, List<VideoDto> items) {
        return wrapped.execute(query, items).stream()
                .filter(v -> v.getDuration() >= minSec && v.getDuration() <= maxSec)
                .collect(Collectors.toList());
    }
}
