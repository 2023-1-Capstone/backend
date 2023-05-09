package com.capstone.carbonlive.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WaterPredictionFeeResponse {
    private Integer data;
    private Integer prediction;
    private Integer fee;
}
