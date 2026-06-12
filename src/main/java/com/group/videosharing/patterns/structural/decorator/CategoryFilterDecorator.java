package com.group.videosharing.patterns.structural.decorator;

import com.group.videosharing.dto.VideoDto;
import com.group.videosharing.patterns.behavioral.strategy.SearchStrategy;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryFilterDecorator extends SearchResultDecorator {
    private final String category;

    public CategoryFilterDecorator(SearchStrategy wrapped, String category) {
        super(wrapped);
        this.category = category;
    }

    @Override
    public List<VideoDto> execute(String query, List<VideoDto> items) {
        return wrapped.execute(query, items).stream()
                .filter(v -> category.equalsIgnoreCase(v.getCategory()))
                .collect(Collectors.toList());
    }
}
