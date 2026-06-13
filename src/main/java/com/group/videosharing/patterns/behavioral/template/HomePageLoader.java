package com.group.videosharing.patterns.behavioral.template;

import com.group.videosharing.dto.UserDto;
import com.group.videosharing.dto.VideoDto;
import com.group.videosharing.patterns.behavioral.strategy.RecommendationStrategy;
import java.util.List;
import java.util.function.Supplier;

public class HomePageLoader extends ContentLoader {
    private final Supplier<List<VideoDto>> source;
    private final RecommendationStrategy recommendationStrategy;
    private final UserDto viewer;

    public HomePageLoader(Supplier<List<VideoDto>> source,
                          RecommendationStrategy recommendationStrategy,
                          UserDto viewer) {
        this.source = source;
        this.recommendationStrategy = recommendationStrategy;
        this.viewer = viewer;
    }

    @Override
    protected List<VideoDto> fetchData() {
        return source.get();
    }

    @Override
    protected List<VideoDto> processData(List<VideoDto> data) {
        return recommendationStrategy.recommend(viewer, data);
    }
}
