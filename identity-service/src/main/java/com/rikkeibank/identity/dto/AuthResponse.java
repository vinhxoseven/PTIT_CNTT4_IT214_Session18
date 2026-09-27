package com.rikkeibank.identity.dto;
public record AuthResponse(String accessToken,String refreshToken,String tokenType,long expiresInSeconds){}
