package com.ivangochev.raceratingapi.user.mapper;

import com.ivangochev.raceratingapi.race.Race;
import com.ivangochev.raceratingapi.user.User;
import com.ivangochev.raceratingapi.user.dto.UserDto;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getImageUrl(),
                user.getRole(),
                user.getVotedForRaces().stream().map(Race::getId).collect(Collectors.toList())
        );
    }
}
