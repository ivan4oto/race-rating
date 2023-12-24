package com.ivangochev.raceratingapi.mapper;

import com.ivangochev.raceratingapi.model.User;
import com.ivangochev.raceratingapi.rest.dto.UserDto;

public interface UserMapper {

    UserDto toUserDto(User user);
}