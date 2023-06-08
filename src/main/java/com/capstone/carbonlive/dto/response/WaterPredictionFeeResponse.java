package com.capstone.carbonlive.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaterPredictionFeeResponse {
    private Integer data;
    private Integer prediction;
    private Integer fee;
    private Integer fee_prediction;
}
