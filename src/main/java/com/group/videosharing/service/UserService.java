package com.group.videosharing.service;

import com.group.videosharing.domain.UserEntity;
import com.group.videosharing.dto.UserDto;
import com.group.videosharing.patterns.structural.facade.IUserService;
import com.group.videosharing.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto getUserById(String userId) {
        validateId(userId, "userId");
        return userRepository.findById(userId)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
    }

    private UserDto toDto(UserEntity user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setChannelId(user.getId());
        return dto;
    }

    private void validateId(String id, String fieldName) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }
}
