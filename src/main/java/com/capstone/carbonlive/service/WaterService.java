package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;

public interface WaterService {
    UsageResult<UsageResponse> getAll();
}
