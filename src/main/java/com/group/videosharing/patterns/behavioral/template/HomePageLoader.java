package com.group.videosharing.patterns.behavioral.template;

import com.group.videosharing.dto.UserDto;
import com.group.videosharing.dto.VideoDto;
import com.group.videosharing.patterns.behavioral.strategy.RecommendationStrategy;
import java.util.List;

/**
 * Template Method — Pattern 3: HomePageLoader
 * Khung quy trình: fetchData → processData → sortData → trackAnalytics
 * processData() áp dụng RecommendationStrategy (Guest hoặc Personalized).
 */
public class HomePageLoader extends ContentLoader {

    private final List<VideoDto>        allVideos;
    private final RecommendationStrategy strategy;
    private final UserDto               currentUser;

    /**
     * @param allVideos   danh sách video public đã load từ service
     * @param strategy    GuestRecommendationStrategy hoặc PersonalizedRecommendationStrategy
     * @param currentUser user hiện tại (null nếu chưa đăng nhập)
     */
    public HomePageLoader(List<VideoDto> allVideos, RecommendationStrategy strategy, UserDto currentUser) {
        this.allVideos   = allVideos;
        this.strategy    = strategy;
        this.currentUser = currentUser;
    }

    /** fetchData: trả về toàn bộ video đã inject */
    @Override
    protected List<VideoDto> fetchData() {
        return allVideos;
    }

    /** processData: áp dụng RecommendationStrategy để cá nhân hóa feed */
    @Override
    protected List<VideoDto> processData(List<VideoDto> data) {
        return strategy.recommend(currentUser, data);
    }
}
