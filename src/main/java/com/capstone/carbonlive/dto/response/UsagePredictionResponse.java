package com.capstone.carbonlive.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsagePredictionResponse {
    private Integer data;
    private Integer prediction;
}
