package com.capstone.carbonlive.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageFeeResponse {
    private Integer usages;
    private Integer fee;
    private Integer prediction;
    private Integer fee_prediction;
}
