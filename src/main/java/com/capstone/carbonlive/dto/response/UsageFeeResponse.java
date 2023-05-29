package com.capstone.carbonlive.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsageFeeResponse {
    private Integer usages;
    private Integer fee;
}