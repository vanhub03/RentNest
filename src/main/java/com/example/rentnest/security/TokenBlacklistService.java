package com.example.rentnest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //luu token vao blacklist
    public void blacklistToken(String token, long expirationMs){
        stringRedisTemplate.opsForValue().set(token, "blacklisted", expirationMs, TimeUnit.MILLISECONDS);
    }

    //kiem tra xem token co nam trong redis
    public boolean isTokenBlacklisted(String token){
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(token));
    }
}
