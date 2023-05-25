package com.capstone.carbonlive.dto.request;

import lombok.Data;

@Data
public class JoinConfirmRequest {
    private String email;
    private String authToken;
}
