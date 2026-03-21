package com.artwell.api.dto;

import com.artwell.api.enums.UserRole;

import java.util.UUID;

public record LoginResponse(
        String accessToken,
        String tokenType,
        int expiresIn,
        UserRole role,
        UUID userId,
        String displayName
) {}
