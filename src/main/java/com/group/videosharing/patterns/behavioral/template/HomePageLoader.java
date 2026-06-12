package com.group.videosharing.patterns.behavioral.template;

import com.group.videosharing.dto.VideoDto;
import java.util.List;

public class HomePageLoader extends ContentLoader {

    @Override
    protected List<VideoDto> fetchData() {
        // TODO: gọi recommendation service
        return List.of();
    }

    @Override
    protected List<VideoDto> processData(List<VideoDto> data) {
        // TODO: cá nhân hóa theo session user
        return data;
    }
}
