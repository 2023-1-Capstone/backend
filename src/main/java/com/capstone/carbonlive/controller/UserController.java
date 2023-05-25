package com.capstone.carbonlive.controller;

import com.capstone.carbonlive.dto.request.LoginRequest;
import com.capstone.carbonlive.dto.request.ReissueRequest;
import com.capstone.carbonlive.dto.request.UpdatePasswordRequest;
import com.capstone.carbonlive.dto.request.UserJoinRequest;
import com.capstone.carbonlive.security.UserDetailsImpl;
import com.capstone.carbonlive.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


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
    public ResponseEntity<?> joinConfirm(@RequestParam("email") String email, @RequestParam("authToken") String authToken) {
        userService.joinConfirm(email, authToken);

        return ResponseEntity.ok().build();
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest, HttpServletResponse response) {
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
    public ResponseEntity<?> reissue(@RequestBody @Valid ReissueRequest reissueRequest, HttpServletResponse response) {
        userService.reissue(reissueRequest, response);

        return ResponseEntity.ok().build();
    }

    /**
     * 비밀번호 찾기
     * -임시 비밀번호 발급
     * -로그인으로 리다이렉트
     */
    @GetMapping("/password")
    public ResponseEntity<?> findPassword(@RequestParam("username") String username, @RequestParam("name") String name) {
        userService.findPassword(username, name);

        return ResponseEntity.ok().build();
    }

    /**
     * 비밀번호 수정
     */
    @PatchMapping("/newPw")
    public ResponseEntity<?> updatePassword(Authentication authentication, @RequestBody @Valid UpdatePasswordRequest passwordRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        userService.updatePassword(userDetails.getUsername(), passwordRequest);

        return ResponseEntity.ok().build();
    }
}
