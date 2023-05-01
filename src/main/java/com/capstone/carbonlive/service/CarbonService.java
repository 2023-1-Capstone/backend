package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.CarbonYearResponse;
import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;

public interface CarbonService {
    UsageResult<CarbonYearResponse> getYearsUsages();
    UsageResult<UsageResponse> getBuildingUsages(Long buildingId);
}
