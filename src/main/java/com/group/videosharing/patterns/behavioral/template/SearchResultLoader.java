package com.group.videosharing.patterns.behavioral.template;

import com.group.videosharing.dto.VideoDto;
import com.group.videosharing.patterns.behavioral.strategy.SearchContext;
import java.util.List;
import java.util.function.Supplier;

public class SearchResultLoader extends ContentLoader {
    private final String query;
    private final Supplier<List<VideoDto>> source;
    private final SearchContext searchContext;

    public SearchResultLoader(String query, Supplier<List<VideoDto>> source, SearchContext searchContext) {
        this.query = query;
        this.source = source;
        this.searchContext = searchContext;
    }

    @Override
    protected List<VideoDto> fetchData() {
        return source.get();
    }

    @Override
    protected List<VideoDto> processData(List<VideoDto> data) { return data; }

    @Override
    protected List<VideoDto> sortData(List<VideoDto> data) {
        return searchContext.search(query, data);
    }
}
