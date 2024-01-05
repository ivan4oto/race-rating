package com.ivangochev.raceratingapi.rest.dto;

public record UserResponseDTO(Long id, String username, String name, String email, String imageUrl) {
}