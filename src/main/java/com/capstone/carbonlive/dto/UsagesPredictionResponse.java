package com.capstone.carbonlive.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsagesPredictionResponse {
    private int data;
    private boolean prediction;
}
