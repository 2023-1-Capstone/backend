package com.capstone.carbonlive.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    ALREADY_EXIST_USER(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디입니다."),
    ALREADY_EXIST_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    NO_USER(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    NO_LOGIN(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),

    NO_CERTIFICATION(HttpStatus.UNAUTHORIZED, "인증되지 않았습니다."),

    INVALID_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 엑세스 토큰입니다."),
    NO_REFRESH_TOKEN(HttpStatus.NOT_FOUND, "존재하지 않는 리프레시 토큰입니다."),
    WRONG_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "잘못된 리프레시 토큰입니다."),
    NO_AUTH_TOKEN(HttpStatus.NOT_FOUND, "존재하지 않는 인증 토큰입니다."),

    DUPLICATED_REQUEST(HttpStatus.BAD_REQUEST, "중복된 요청입니다."),

    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다"),
    WRONG_TYPE_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 타입의 토큰입니다"),
    UNKNOWN_ERROR(HttpStatus.UNAUTHORIZED, "알 수 없는 에러입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "지원되지 않는 형식의 토큰입니다."),

    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 불가");

    private final HttpStatus httpStatus;
    private final String message;
}
