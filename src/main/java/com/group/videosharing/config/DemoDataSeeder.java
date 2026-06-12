package com.group.videosharing.config;

import com.group.videosharing.domain.UserEntity;
import com.group.videosharing.domain.VideoEntity;
import com.group.videosharing.repository.UserRepository;
import com.group.videosharing.repository.VideoRepository;
import com.group.videosharing.service.SubscriptionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DemoDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final SubscriptionService subscriptionService;

    public DemoDataSeeder(UserRepository userRepository,
                          VideoRepository videoRepository,
                          SubscriptionService subscriptionService) {
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
        this.subscriptionService = subscriptionService;
    }

    @Override
    public void run(String... args) {
        UserEntity demoCreator = findOrCreateUser(
                "demo_creator",
                "demo.creator@example.com",
                "Demo Creator",
                "https://example.com/avatars/demo-creator.png");
        UserEntity springGuru = findOrCreateUser(
                "spring_guru",
                "spring.guru@example.com",
                "Spring Guru",
                "https://example.com/avatars/spring-guru.png");

        if (videoRepository.count() == 0) {
            seedVideos(demoCreator, springGuru);
        }

        subscriptionService.subscribe(springGuru.getId(), demoCreator.getId());
        subscriptionService.subscribe(demoCreator.getId(), springGuru.getId());
    }

    private UserEntity findOrCreateUser(String username, String email, String channelName, String avatarUrl) {
        return userRepository.findByUsername(username)
                .orElseGet(() -> {
                    UserEntity user = new UserEntity();
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setPasswordHash("demo-password-hash");
                    user.setChannelName(channelName);
                    user.setAvatarUrl(avatarUrl);
                    return userRepository.save(user);
                });
    }

    private void seedVideos(UserEntity demoCreator, UserEntity springGuru) {
        videoRepository.save(video(
                "Design Patterns trong Mini YouTube",
                "Demo cách áp dụng Facade, Builder, Observer và Singleton.",
                demoCreator.getId(),
                "Education",
                480,
                "https://example.com/thumbs/design-patterns.jpg",
                "https://example.com/videos/design-patterns.mp4"));
        videoRepository.save(video(
                "Spring Boot REST API cơ bản",
                "Xây dựng REST API cho ứng dụng chia sẻ video.",
                springGuru.getId(),
                "Programming",
                720,
                "https://example.com/thumbs/spring-rest.jpg",
                "https://example.com/videos/spring-rest.mp4"));
        videoRepository.save(video(
                "Observer Pattern với EventBus",
                "Minh họa publish/subscribe trong backend Java.",
                demoCreator.getId(),
                "Design Patterns",
                360,
                "https://example.com/thumbs/eventbus.jpg",
                "https://example.com/videos/eventbus.mp4"));
        videoRepository.save(video(
                "Upload Video bằng Builder Pattern",
                "Tạo metadata video bằng fluent builder.",
                demoCreator.getId(),
                "Design Patterns",
                420,
                "https://example.com/thumbs/upload-builder.jpg",
                "https://example.com/videos/upload-builder.mp4"));
        videoRepository.save(video(
                "JPA Repository cho người mới",
                "Lưu và truy vấn dữ liệu với Spring Data JPA.",
                springGuru.getId(),
                "Programming",
                600,
                "https://example.com/thumbs/jpa-repository.jpg",
                "https://example.com/videos/jpa-repository.mp4"));
    }

    private VideoEntity video(String title,
                              String description,
                              String uploaderId,
                              String category,
                              int durationSeconds,
                              String thumbnailUrl,
                              String videoUrl) {
        return new VideoEntity.Builder()
                .setTitle(title)
                .setDescription(description)
                .setVisibility("public")
                .setUploaderId(uploaderId)
                .setCategory(category)
                .setDurationSeconds(durationSeconds)
                .setThumbnail(thumbnailUrl)
                .setVideoUrl(videoUrl)
                .build();
    }
}
