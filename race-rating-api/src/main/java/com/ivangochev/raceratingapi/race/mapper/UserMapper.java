package com.ivangochev.raceratingapi.race.mapper;

import com.ivangochev.raceratingapi.user.User;
import com.ivangochev.raceratingapi.user.dto.UserDto;

public interface UserMapper {

    UserDto toUserDto(User user);
}