package com.ivangochev.raceratingapi.user.dto;

import java.util.List;

public record UserDto(
        Long id,
        String username,
        String name,
        String email,
        String imageUrl,
        String role,
        List<Long> votedForRaces
) {
}