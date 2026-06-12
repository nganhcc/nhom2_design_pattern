package com.group.videosharing.service;

import com.group.videosharing.domain.UserEntity;
import com.group.videosharing.dto.UserDto;
import com.group.videosharing.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTest {

    private FakeUserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = new FakeUserRepository();
        userService = new UserService(userRepository.proxy());
    }

    @Test
    void getUserByIdReturnsMappedDtoWhenFound() {
        userRepository.userById = Optional.of(user("user-1"));

        UserDto result = userService.getUserById("user-1");

        assertEquals("user-1", userRepository.lastFindById);
        assertEquals("user-1", result.getId());
        assertEquals("member4", result.getUsername());
        assertEquals("member4@example.com", result.getEmail());
        assertEquals("https://example.com/avatar.jpg", result.getAvatarUrl());
        assertEquals("user-1", result.getChannelId());
    }

    @Test
    void getUserByIdRejectsBlankId() {
        assertThrows(IllegalArgumentException.class, () -> userService.getUserById(" "));
    }

    @Test
    void getUserByIdThrowsWhenUserDoesNotExist() {
        userRepository.userById = Optional.empty();

        assertThrows(NoSuchElementException.class, () -> userService.getUserById("missing"));
    }

    private static class FakeUserRepository {
        private Optional<UserEntity> userById = Optional.empty();
        private String lastFindById;

        private UserRepository proxy() {
            return (UserRepository) Proxy.newProxyInstance(
                    UserRepository.class.getClassLoader(),
                    new Class<?>[]{UserRepository.class},
                    (target, method, args) -> {
                        if (method.getName().equals("findById")) {
                            lastFindById = (String) args[0];
                            return userById;
                        }
                        if (method.getName().equals("toString")) {
                            return "FakeUserRepository";
                        }
                        throw new UnsupportedOperationException("Unsupported method: " + method.getName());
                    });
        }
    }

    private UserEntity user(String id) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUsername("member4");
        user.setEmail("member4@example.com");
        user.setPasswordHash("hash");
        user.setChannelName("Member 4 Channel");
        user.setAvatarUrl("https://example.com/avatar.jpg");
        return user;
    }
}
