package com.example.hubspotintegration.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimiterService {

    private final StringRedisTemplate redisTemplate;

    public RateLimiterService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(String key, int maxRequests, int windowInSeconds) {
        String redisKey = "rate_limit:" + key;
        Long requests = redisTemplate.opsForValue().increment(redisKey);

        if (requests == 1) {
            redisTemplate.expire(redisKey, Duration.ofSeconds(windowInSeconds));
        }

        return requests != null && requests <= maxRequests;
    }
}
