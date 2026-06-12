package com.group.videosharing.patterns.behavioral.template;

import com.group.videosharing.dto.VideoDto;
import java.util.List;

/** Template Method — Pattern 3 */
public abstract class ContentLoader {

    /** Template method — final, không override */
    public final List<VideoDto> loadPage() {
        List<VideoDto> data      = fetchData();
        List<VideoDto> processed = processData(data);
        List<VideoDto> sorted    = sortData(processed);
        trackAnalytics(sorted);
        return sorted;
    }

    protected abstract List<VideoDto> fetchData();
    protected abstract List<VideoDto> processData(List<VideoDto> data);

    /** Hook — subclass có thể override hoặc dùng default */
    protected List<VideoDto> sortData(List<VideoDto> data) { return data; }
    protected void trackAnalytics(List<VideoDto> data)     { /* default: no-op */ }
}
