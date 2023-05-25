package com.capstone.carbonlive.dto.request;

import lombok.Data;

@Data
public class ReissueRequest {
    private String username;
    private String refreshToken;
}
