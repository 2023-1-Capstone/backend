package com.capstone.carbonlive.dto.request;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String currentPw;
    private String newPw;
}
