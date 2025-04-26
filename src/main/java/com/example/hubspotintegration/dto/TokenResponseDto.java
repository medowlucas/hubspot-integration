package com.example.hubspotintegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TokenResponseDto {
    @JsonProperty("access_token")
    private String accessToken;
    
    @JsonProperty("refresh_token")
    private String refreshToken;
    
    @JsonProperty("expires_in")
    private int expiresIn;
    
    @JsonProperty("token_type")
    private String tokenType;
}