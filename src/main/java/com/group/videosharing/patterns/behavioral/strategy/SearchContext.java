package com.group.videosharing.patterns.behavioral.strategy;

import com.group.videosharing.dto.VideoDto;
import java.util.List;

public class SearchContext {
    private SearchStrategy strategy;

    public SearchContext(SearchStrategy strategy) { this.strategy = strategy; }

    public void setStrategy(SearchStrategy strategy) { this.strategy = strategy; }

    public List<VideoDto> search(String query, List<VideoDto> items) {
        return strategy.execute(query, items);
    }
}
