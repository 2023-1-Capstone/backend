package com.capstone.carbonlive.service.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate redisTemplate;

    public String getData(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void setDataWithExpiration(String key, String value, Long time) {
        if (this.getData(key) != null)
            this.deleteData(key);
        redisTemplate.opsForValue().set(key, value, time, TimeUnit.MILLISECONDS);
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }
}
