package com.ivangochev.raceratingapi.user.mapper;

import com.ivangochev.raceratingapi.user.User;
import com.ivangochev.raceratingapi.user.dto.UserDto;

public interface UserMapper {

    UserDto toUserDto(User user);
    User fromUserDto(UserDto userDto, User user);
}