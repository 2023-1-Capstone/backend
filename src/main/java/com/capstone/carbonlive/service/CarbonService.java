package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.response.CarbonYearResponse;
import com.capstone.carbonlive.dto.response.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.dto.response.UsageWithNameResponse;

public interface CarbonService {
    UsageResult<CarbonYearResponse> getYearsUsages();
    UsageResult<UsageResponse> getBuildingUsages(Long buildingId);
    UsageResult<UsageWithNameResponse> getAllUsages();
}
