package com.capstone.carbonlive.errors;

import lombok.*;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private ErrorCode code;
    private String message;
}
