package com.jazzybruno.example.v1.dto.responses;

public record UserDTO(
        Long userId,
        String email,
        String username,
        String national_id
        ) {
}
