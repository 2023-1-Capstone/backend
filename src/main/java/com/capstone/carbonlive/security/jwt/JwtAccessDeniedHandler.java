package com.capstone.carbonlive.security.jwt;

import com.capstone.carbonlive.errors.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.capstone.carbonlive.errors.ErrorCode.ACCESS_DENIED;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        setResponse(response, ACCESS_DENIED);

    }

    private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", errorCode.getMessage());
        jsonObject.put("code", errorCode.getHttpStatus());

        response.getWriter().print(jsonObject);
    }
}
