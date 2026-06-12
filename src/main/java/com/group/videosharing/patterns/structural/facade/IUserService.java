package com.group.videosharing.patterns.structural.facade;

import com.group.videosharing.dto.UserDto;

public interface IUserService {
    UserDto getUserById(String userId);
}
