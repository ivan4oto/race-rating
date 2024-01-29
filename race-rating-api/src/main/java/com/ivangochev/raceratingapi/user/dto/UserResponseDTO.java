package com.ivangochev.raceratingapi.user.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public final class UserResponseDTO implements Serializable {
    private final Long id;
    private final String username;
    private final String name;
    private final String email;
    private final String imageUrl;
}