package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.dto.response.WaterResponse;

public interface WaterService {
    UsageResult<WaterResponse> getAll();
}
