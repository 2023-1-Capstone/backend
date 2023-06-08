package com.capstone.carbonlive.controller;

import com.capstone.carbonlive.dto.request.*;
import com.capstone.carbonlive.security.UserDetailsImpl;
import com.capstone.carbonlive.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    /**
     * 회원 가입
     * -로그인으로 리다이렉트
     */
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid UserJoinRequest userJoinRequest) {
        //DB user insert & 인증 메일 발송
        userService.join(userJoinRequest);

        return ResponseEntity.ok().build();
    }

    /**
     * 회원가입 이메일 인증 성공
     */
    @GetMapping("/joinConfirm")
    public void joinConfirm(@RequestParam("email") String email,
                            @RequestParam("authToken") String authToken,
                            HttpServletResponse response) throws IOException {
        String uri = userService.joinConfirm(email, authToken);
        if (uri != null) {
            response.sendRedirect(uri);
        }
        else {
            response.sendRedirect("https://carbonlive.kro.kr");
        }
        return;
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        userService.login(loginRequest, response);

        return ResponseEntity.ok().build();
    }

    /**
     * 로그아웃
     * -시작화면으로 리다이렉트
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        userService.logout(request);

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok().build();
    }

    /**
     * 토큰 재발급
     */
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody ReissueRequest reissueRequest,
            @CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {

        userService.reissue(reissueRequest.getUsername(), refreshToken, response);

        return ResponseEntity.ok().build();
    }

    /**
     * 비밀번호 찾기
     * -임시 비밀번호 발급
     * -로그인으로 리다이렉트
     */
    @PostMapping("/password")
    public ResponseEntity<?> findPassword(@RequestBody FindPasswordRequest findPasswordRequest) {
        userService.findPassword(findPasswordRequest.getUsername(), findPasswordRequest.getName());

        return ResponseEntity.ok().build();
    }

    /**
     * 비밀번호 수정
     */
    @PatchMapping("/password")
    public ResponseEntity<?> updatePassword(Authentication authentication, @RequestBody UpdatePasswordRequest passwordRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        userService.updatePassword(userDetails.getUsername(), passwordRequest);

        return ResponseEntity.ok().build();
    }
}
