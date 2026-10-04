package com.company.enterprise.auth.dto;

public record RefreshTokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        String refreshToken,
        UserSummary user
) {}
