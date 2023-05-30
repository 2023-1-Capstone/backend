package com.capstone.carbonlive.security.jwt;

import com.capstone.carbonlive.errors.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.capstone.carbonlive.errors.ErrorCode.*;


@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        String exception = (String) request.getAttribute("exception");

        //토큰 없는 경우
        if (exception == null) {
            setResponse(response, UNKNOWN_ERROR);
        }
        //잘못된 타입의 토큰인 경우
        else if (exception.equals(WRONG_TYPE_TOKEN)) {
            setResponse(response, WRONG_TYPE_TOKEN);
        }
        //만료된 토큰인 경우
        else if (exception.equals(EXPIRED_TOKEN)) {
            setResponse(response, EXPIRED_TOKEN);
        }
        //지원되지 않는 토큰인 경우
        else if (exception.equals(UNSUPPORTED_TOKEN)) {
            setResponse(response, UNSUPPORTED_TOKEN);
        }
        else {
            setResponse(response, ACCESS_DENIED);
        }
    }

    private void setResponse(HttpServletResponse response, ErrorCode code) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", code.getHttpStatus().value());
        jsonObject.put("code", code.getHttpStatus());
        jsonObject.put("message", code.getMessage());

        response.getWriter().print(jsonObject);
    }
}
