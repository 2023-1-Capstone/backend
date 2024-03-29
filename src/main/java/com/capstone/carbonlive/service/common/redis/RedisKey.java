package com.capstone.carbonlive.service.common.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 이메일 인증      key(EMAILAUTH_email),         value(UUID)
 * Refresh Token  key(REFRESH_username),        value(refresh Token)
 * BlackList      key(BLACKLIST_access Token),  value(access Token)
 * Validation     key(VALIDATION),              value(username)
 */

@Getter
@AllArgsConstructor
public enum RedisKey {
    EMAILAUTH("EMAILAUTH_"), REFRESH("REFRESH_"), BLACKLIST("BLACKLIST_"), VALIDATION("VALIDATION");

    private String key;
}
