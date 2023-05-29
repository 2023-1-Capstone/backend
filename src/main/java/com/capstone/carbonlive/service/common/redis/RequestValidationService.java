package com.capstone.carbonlive.service.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.capstone.carbonlive.service.common.redis.RedisKey.VALIDATION;

/**
 * 중복 요청 방지 처리를 위한 서비스
 */
@Service
@RequiredArgsConstructor
public class RequestValidationService {

    private final StringRedisTemplate stringRedisTemplate;

    public boolean isDuplicatedRequest() {
        return stringRedisTemplate.opsForValue().get(VALIDATION.getKey()) != null;
    }

    public void save(String username) {
        stringRedisTemplate.opsForValue().set(VALIDATION.getKey(), username, 1, TimeUnit.SECONDS);
    }

    public String get() {
        return stringRedisTemplate.opsForValue().get(VALIDATION.getKey());
    }

}
