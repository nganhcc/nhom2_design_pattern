package com.group.videosharing.patterns.behavioral.strategy;

import com.group.videosharing.dto.UserDto;
import com.group.videosharing.dto.VideoDto;
import com.group.videosharing.service.SubscriptionService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PersonalizedRecommendationStrategy implements RecommendationStrategy {

    private final SubscriptionService subscriptionService;

    public PersonalizedRecommendationStrategy(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Override
    public List<VideoDto> recommend(UserDto user, List<VideoDto> pool) {
        // Fallback về GuestStrategy khi chưa đăng nhập
        if (user == null) {
            return new GuestRecommendationStrategy().recommend(null, pool);
        }

        String userId = user.getId();
        // Ưu tiên video từ kênh đã subscribe, sort viewCount giảm dần trong mỗi nhóm
        List<VideoDto> subscribed = pool.stream()
                .filter(v -> subscriptionService.isSubscribed(userId, v.getChannelId()))
                .sorted(Comparator.comparingLong(VideoDto::getViewCount).reversed())
                .toList();

        List<VideoDto> others = pool.stream()
                .filter(v -> !subscriptionService.isSubscribed(userId, v.getChannelId()))
                .sorted(Comparator.comparingLong(VideoDto::getViewCount).reversed())
                .toList();

        // Hợp nhất, không trùng lặp
        Set<VideoDto> result = new LinkedHashSet<>(subscribed);
        result.addAll(others);
        return new ArrayList<>(result);
    }
}
