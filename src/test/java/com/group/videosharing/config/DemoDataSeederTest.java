package com.group.videosharing.config;

import com.group.videosharing.domain.UserEntity;
import com.group.videosharing.domain.VideoEntity;
import com.group.videosharing.repository.UserRepository;
import com.group.videosharing.repository.VideoRepository;
import com.group.videosharing.service.SubscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DemoDataSeederTest {

    private FakeUserRepository userRepository;
    private FakeVideoRepository videoRepository;
    private SubscriptionService subscriptionService;
    private DemoDataSeeder seeder;

    @BeforeEach
    void setUp() {
        userRepository = new FakeUserRepository();
        videoRepository = new FakeVideoRepository();
        subscriptionService = new SubscriptionService();
        seeder = new DemoDataSeeder(
                userRepository.proxy(),
                videoRepository.proxy(),
                subscriptionService);
    }

    @Test
    void runSeedsUsersVideosAndSubscriptionsWhenRepositoriesAreEmpty() {
        seeder.run();

        assertEquals(2, userRepository.usersByUsername.size());
        assertTrue(userRepository.usersByUsername.containsKey("demo_creator"));
        assertTrue(userRepository.usersByUsername.containsKey("spring_guru"));
        assertEquals(5, videoRepository.savedVideos.size());

        String demoCreatorId = userRepository.usersByUsername.get("demo_creator").getId();
        String springGuruId = userRepository.usersByUsername.get("spring_guru").getId();
        assertTrue(videoRepository.savedVideos.stream()
                .anyMatch(video -> video.getUploaderId().equals(demoCreatorId)));
        assertTrue(videoRepository.savedVideos.stream()
                .anyMatch(video -> video.getUploaderId().equals(springGuruId)));
        assertTrue(subscriptionService.isSubscribed(springGuruId, demoCreatorId));
        assertTrue(subscriptionService.isSubscribed(demoCreatorId, springGuruId));
        assertEquals(1, subscriptionService.getSubscriberCount(demoCreatorId));
        assertEquals(1, subscriptionService.getSubscriberCount(springGuruId));
    }

    @Test
    void runDoesNotCreateDuplicateUsersWhenTheyAlreadyExist() {
        userRepository.usersByUsername.put("demo_creator", existingUser("user-existing-1", "demo_creator"));
        userRepository.usersByUsername.put("spring_guru", existingUser("user-existing-2", "spring_guru"));

        seeder.run();

        assertEquals(2, userRepository.usersByUsername.size());
        assertEquals(0, userRepository.saveUserCount);
    }

    @Test
    void runDoesNotSeedVideosWhenVideoRepositoryIsNotEmpty() {
        videoRepository.existingVideoCount = 3;

        seeder.run();

        assertEquals(0, videoRepository.savedVideos.size());
        assertEquals(2, userRepository.usersByUsername.size());
    }

    @Test
    void seededVideosArePublicAndHaveDemoMetadata() {
        seeder.run();

        assertFalse(videoRepository.savedVideos.isEmpty());
        assertTrue(videoRepository.savedVideos.stream().allMatch(video -> video.getVisibility().equals("public")));
        assertTrue(videoRepository.savedVideos.stream().allMatch(video -> video.getTitle() != null));
        assertTrue(videoRepository.savedVideos.stream().allMatch(video -> video.getVideoUrl() != null));
        assertTrue(videoRepository.savedVideos.stream().allMatch(video -> video.getDurationSeconds() > 0));
    }

    private UserEntity existingUser(String id, String username) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setPasswordHash("hash");
        user.setChannelName(username);
        return user;
    }

    private static class FakeUserRepository {
        private final Map<String, UserEntity> usersByUsername = new LinkedHashMap<>();
        private int saveUserCount;

        private UserRepository proxy() {
            return (UserRepository) Proxy.newProxyInstance(
                    UserRepository.class.getClassLoader(),
                    new Class<?>[]{UserRepository.class},
                    (target, method, args) -> {
                        if (method.getName().equals("findByUsername")) {
                            return Optional.ofNullable(usersByUsername.get((String) args[0]));
                        }
                        if (method.getName().equals("save")) {
                            saveUserCount++;
                            UserEntity user = (UserEntity) args[0];
                            if (user.getId() == null) {
                                user.setId("user-" + (usersByUsername.size() + 1));
                            }
                            usersByUsername.put(user.getUsername(), user);
                            return user;
                        }
                        if (method.getName().equals("toString")) {
                            return "FakeUserRepository";
                        }
                        throw new UnsupportedOperationException("Unsupported method: " + method.getName());
                    });
        }
    }

    private static class FakeVideoRepository {
        private final List<VideoEntity> savedVideos = new ArrayList<>();
        private long existingVideoCount;

        private VideoRepository proxy() {
            return (VideoRepository) Proxy.newProxyInstance(
                    VideoRepository.class.getClassLoader(),
                    new Class<?>[]{VideoRepository.class},
                    (target, method, args) -> {
                        if (method.getName().equals("count")) {
                            return existingVideoCount + savedVideos.size();
                        }
                        if (method.getName().equals("save")) {
                            VideoEntity video = (VideoEntity) args[0];
                            ReflectionTestUtils.setField(video, "id", "video-" + (savedVideos.size() + 1));
                            savedVideos.add(video);
                            return video;
                        }
                        if (method.getName().equals("toString")) {
                            return "FakeVideoRepository";
                        }
                        throw new UnsupportedOperationException("Unsupported method: " + method.getName());
                    });
        }
    }
}
