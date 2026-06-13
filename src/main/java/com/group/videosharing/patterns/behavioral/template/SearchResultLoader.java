package com.group.videosharing.patterns.behavioral.template;

import com.group.videosharing.dto.VideoDto;
import com.group.videosharing.patterns.behavioral.strategy.SearchContext;
import com.group.videosharing.patterns.behavioral.strategy.SearchStrategy;
import java.util.List;
import java.util.function.Supplier;

/**
 * Template Method — Pattern 3: SearchResultLoader
 * Khung quy trình: fetchData → processData → sortData → trackAnalytics
 * Subclass override fetchData() và sortData() để tùy chỉnh hành vi.
 */
public class SearchResultLoader extends ContentLoader {

    private final List<VideoDto> allVideos;
    private final String         query;
    private final SearchContext  context;

    /**
     * @param allVideos danh sách video đã load sẵn từ service
     * @param query     từ khóa tìm kiếm (có thể null/blank = không lọc keyword)
     * @param strategy  SearchStrategy đã được wrap bởi Decorator chain (nếu có)
     */
    public SearchResultLoader(List<VideoDto> allVideos, String query, SearchStrategy strategy) {
        this.allVideos = allVideos;
        this.query     = query;
        this.context   = new SearchContext(strategy);
    }

    /** fetchData: trả về toàn bộ video đã inject từ ngoài */
    @Override
    protected List<VideoDto> fetchData() {
        return allVideos;
    }

    /** processData: giữ nguyên (không biến đổi data ở bước này) */
    @Override
    protected List<VideoDto> processData(List<VideoDto> data) {
        return data;
    }

    /** sortData: áp dụng Strategy + Decorator chain qua SearchContext */
    @Override
    protected List<VideoDto> sortData(List<VideoDto> data) {
        return context.search(query, data);
    }
}
