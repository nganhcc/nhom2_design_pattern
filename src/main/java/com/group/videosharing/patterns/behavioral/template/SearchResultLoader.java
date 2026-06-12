package com.group.videosharing.patterns.behavioral.template;

import com.group.videosharing.dto.VideoDto;
import java.util.List;

public class SearchResultLoader extends ContentLoader {
    private final String query;

    public SearchResultLoader(String query) { this.query = query; }

    @Override
    protected List<VideoDto> fetchData() {
        // TODO: gọi search service với query
        return List.of();
    }

    @Override
    protected List<VideoDto> processData(List<VideoDto> data) { return data; }

    @Override
    protected List<VideoDto> sortData(List<VideoDto> data) {
        // TODO: sort theo strategy đã chọn
        return data;
    }
}
