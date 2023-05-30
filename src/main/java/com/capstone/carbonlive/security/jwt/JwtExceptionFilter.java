package com.capstone.carbonlive.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.capstone.carbonlive.errors.ErrorCode.EXPIRED_TOKEN;
import static com.capstone.carbonlive.errors.ErrorCode.WRONG_TYPE_TOKEN;

/**
 * JWT 관련 오류
 */
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            request.setAttribute("exception", EXPIRED_TOKEN);
        } catch (MalformedJwtException e) {
            request.setAttribute("exception", WRONG_TYPE_TOKEN);
        } catch (SignatureException e) {
            request.setAttribute("exception", WRONG_TYPE_TOKEN);
        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", e.getMessage());
            jsonObject.put("status", e.getHttpStatus().value());
            jsonObject.put("code", e.getErrorCode());

            response.getWriter().print(jsonObject);
        }
    }
}
