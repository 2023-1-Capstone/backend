package com.capstone.carbonlive.security.jwt;

import com.capstone.carbonlive.errors.exception.UserException;
import com.capstone.carbonlive.service.common.redis.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.capstone.carbonlive.errors.ErrorCode.NO_LOGIN;
import static com.capstone.carbonlive.service.common.redis.RedisKey.BLACKLIST;


/**
 * JWT Token 의 유효성을 검증하는 필터
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtTokenProvider.resolveToken(request);

        //Access Token 유효 검증
        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {

            //logout 검증
            checkLogout(accessToken);

            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private void checkLogout(String accessToken) {
        String isLogout = redisService.getData(BLACKLIST.getKey() + accessToken);

        if (StringUtils.hasText(isLogout))
            throw new UserException(NO_LOGIN);
    }
}
