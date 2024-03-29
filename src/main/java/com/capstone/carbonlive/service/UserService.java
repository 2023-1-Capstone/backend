package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.request.LoginRequest;
import com.capstone.carbonlive.dto.request.UpdatePasswordRequest;
import com.capstone.carbonlive.dto.request.UserJoinRequest;
import com.capstone.carbonlive.entity.User;
import com.capstone.carbonlive.entity.UserRole;
import com.capstone.carbonlive.errors.exception.EmailException;
import com.capstone.carbonlive.errors.exception.RequestDuplicationException;
import com.capstone.carbonlive.errors.exception.TokenException;
import com.capstone.carbonlive.errors.exception.UserException;
import com.capstone.carbonlive.repository.UserRepository;
import com.capstone.carbonlive.security.UserDetailsImpl;
import com.capstone.carbonlive.security.jwt.JwtTokenProvider;
import com.capstone.carbonlive.service.common.mail.MailSendService;
import com.capstone.carbonlive.service.common.redis.RedisService;
import com.capstone.carbonlive.service.common.redis.RequestValidationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.UUID;

import static com.capstone.carbonlive.errors.ErrorCode.*;
import static com.capstone.carbonlive.security.jwt.JwtProperties.*;
import static com.capstone.carbonlive.service.common.redis.RedisKey.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    private final RedisService redisService;
    private final RequestValidationService requestValidationService;
    private final MailSendService mailSendService;

    /**
     * 회원가입
     */
    @Transactional
    public void join(UserJoinRequest userJoinRequest) {
        /**
         * 중복요청 방지 처리
         */
        if (requestValidationService.isDuplicatedRequest())
            throw new RequestDuplicationException(DUPLICATED_REQUEST);
        requestValidationService.save(userJoinRequest.getUsername());

        /**
         * 회원가입 로직
         */
        User user = userJoinRequest.toEntity();
        user.encodePassword(passwordEncoder);
        user.setAuthStatus(false);
        user.setRole(UserRole.USER);

        //회원 중복 검증
        validateDuplicatedUser(user);

        //이메일 인증토큰 생성 및 Redis 저장
        String authToken = UUID.randomUUID().toString();
        redisService.setDataWithExpiration(
                EMAILAUTH.getKey() + userJoinRequest.getEmail(), authToken, 5 * 60 * 1000L);

        userRepository.save(user);

        //인증메일 발송
        mailSendService.sendMail(userJoinRequest.getEmail(), authToken);
    }

    private void validateDuplicatedUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent())
            throw new UserException(ALREADY_EXIST_USER);
        if (userRepository.findByEmail(user.getEmail()).isPresent())
            throw new UserException(ALREADY_EXIST_EMAIL);
    }

    /**
     * 회원가입 이메일 인증 성공
     */
    @Transactional
    public String joinConfirm(String email, String authToken) throws IOException {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserException(NO_USER));

        /**
         * 두번째 호출인지 확인
         */
        if (user.isAuthStatus()) {
            return null;
        }

        String redisAuthToken = redisService.getData(EMAILAUTH.getKey() + email);
        if (redisAuthToken == null || !redisAuthToken.equals(authToken)) {
            userRepository.delete(user);
            return "https://carbonlive.kro.kr/authException";
        }

        //인증 상태 업데이트
        user.updateAuthStatus();

        //인증 완료 후 authToken 삭제
        redisService.deleteData(EMAILAUTH.getKey() + email);

        return null;
    }

    /**
     * 로그인
     */
    @Transactional
    public void login(LoginRequest loginRequest, HttpServletResponse response) {
        //인증 객체 생성
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        User user = extractUser(authenticate);

        //이메일 인증 확인
        if (!user.isAuthStatus())
            throw new EmailException(NO_CERTIFICATION);

        // JWT Token 발급
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        //Redis 에 refresh token 저장
        redisService.setDataWithExpiration(
                REFRESH.getKey() + user.getUsername(), refreshToken, REFRESH_TOKEN_VALID_TIME);

        //쿠키에 refresh token 저장
        response.addCookie(createCookie(refreshToken));

        //헤더에 access token 담기
        response.addHeader(HEADER_STRING, accessToken);
    }

    public Cookie createCookie(String refreshToken) {
        String cookieName = "refreshToken";
        String cookieValue = refreshToken;
        Cookie cookie = new Cookie(cookieName, cookieValue);

        cookie.setHttpOnly(true);
        cookie.setSecure(true); //https 설정
        cookie.setPath("/"); //모든 곳에서 열람 가능
        cookie.setMaxAge(60 * 60 * 24);

        return cookie;
    }

    private User extractUser(Authentication authenticate) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authenticate.getPrincipal();
        return userDetails.getUser();
    }

    /**
     * 로그아웃
     */
    @Transactional
    public void logout(HttpServletRequest request) {
        String accessToken = jwtTokenProvider.resolveToken(request);

        //access Token 유효 검증
        if (!jwtTokenProvider.validateToken(accessToken))
            throw new TokenException(INVALID_ACCESS_TOKEN);

        //Redis 에 해당 아이디로 저장된 refresh token 삭제
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        redisService.deleteData(REFRESH.getKey() + userDetails.getUsername());

        //해당 access token BlackList 에 등록
        long expiration = jwtTokenProvider.getExpiration(accessToken);
        redisService.setDataWithExpiration(BLACKLIST.getKey() + accessToken, accessToken, expiration);
    }

    /**
     * 토큰 재발급
     */
    @Transactional
    public void reissue(String username, String refreshToken, HttpServletResponse response) {
        String redisRefreshToken = redisService.getData(REFRESH.getKey() + username);
        if (redisRefreshToken == null)
            throw new TokenException(NO_REFRESH_TOKEN);
        if (!redisRefreshToken.equals(refreshToken))
            throw new TokenException(WRONG_REFRESH_TOKEN);

        //토큰 재발급
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserException(NO_USER));
        String newAccessToken = jwtTokenProvider.generateAccessToken(user);

        //헤더에 access token 담기
        response.addHeader(HEADER_STRING, newAccessToken);
    }

    /**
     * 비밀번호 찾기
     */
    @Transactional
    public void findPassword(String username, String name) {
        //회원정보 일치 확인
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserException(NO_USER));
        if (!user.getName().equals(name))
            throw new UserException(NO_USER);

        //임시 비밀번호 생성
        String tempPassword = getTempPassword();

        //인증 메일 전송
        mailSendService.sendMail_pw(username + "@inha.edu", tempPassword);

        //임시 비밀번호 암호화 DB 업데이트
        String encodedTempPw = passwordEncoder.encode(tempPassword);
        user.updatePw(encodedTempPw);
    }

    private String getTempPassword() {
        char[] charSet = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        String str = "";

        int idx = 0;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
            str += charSet[idx];
        }

        return str;
    }

    /**
     * 비밀번호 일치 검증
     */
    public void checkPw(String username, String password) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserException(NO_USER));
        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new UserException(WRONG_PASSWORD);
    }

    /**
     * 비밀번호 수정
     */
    @Transactional
    public void updatePassword(String username, UpdatePasswordRequest passwordRequest) {
        String currentPw = passwordRequest.getCurrentPw();
        String newPw = passwordRequest.getNewPw();

        //비밀번호 일치 검증
        checkPw(username, currentPw);

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserException(NO_USER));
        String encodedNewPw = passwordEncoder.encode(newPw);
        user.updatePw(encodedNewPw);
    }
}
