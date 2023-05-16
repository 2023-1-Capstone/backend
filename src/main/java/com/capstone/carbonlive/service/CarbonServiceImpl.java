package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.CarbonYearResponse;
import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.dto.UsageWithNameResponse;
import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.entity.Carbon;
import com.capstone.carbonlive.repository.BuildingRepository;
import com.capstone.carbonlive.repository.CarbonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.capstone.carbonlive.service.common.GetUsageResult.getBuildingUsageResult;

@Service
@RequiredArgsConstructor
public class CarbonServiceImpl implements CarbonService{
    private final CarbonRepository carbonRepository;
    private final BuildingRepository buildingRepository;

    @Override
    public UsageResult<CarbonYearResponse> getYearsUsages() {
        UsageResult<CarbonYearResponse> result = new UsageResult<>(new ArrayList<>());
        List<Carbon> carbonList = carbonRepository.findAll(Sort.by("recordedAt").ascending()
                .and(Sort.by("prediction").descending()));
        int year = -1, usages = 0;
        for (Carbon c : carbonList){
            if (c.getUsages() == null)
                continue;

            if (c.getRecordedAt().getYear() != year){
                CarbonYearResponse carbonYearResponse = new CarbonYearResponse(year, usages);
                result.add(carbonYearResponse);
                year = c.getRecordedAt().getYear();
                usages = c.getUsages();
            }
            else{
                usages += c.getUsages();
            }
        }
        if (year > 0){
            CarbonYearResponse carbonYearResponse = new CarbonYearResponse(year, usages);
            result.add(carbonYearResponse);
        }
        result.getResult().remove(0); // 최초 year = -1 인 부분 삭제.

        return result;
    }

    @Override
    public UsageResult<UsageResponse> getBuildingUsages(Long buildingId) {
        Building building = buildingRepository.findById(buildingId).orElseThrow(RuntimeException::new);

        List<Carbon> carbonList = carbonRepository.findByBuildingOrderByRecordedAtAscPredictionDesc(building);

        return getBuildingUsageResult(carbonList);
    }

    @Override
    public UsageResult<UsageWithNameResponse> getAllUsages() {
        UsageResult<UsageWithNameResponse> result = new UsageResult<>(new ArrayList<>());

        List<Building> buildings = buildingRepository.findAll();
        for (Building b : buildings){
            UsageWithNameResponse response = new UsageWithNameResponse();
            response.setName(b.getName());
            UsageResult<UsageResponse> buildingUsages = getBuildingUsages(b.getId());
            response.setUsagesList(buildingUsages.getResult());
            result.add(response);
        }

        return result;
    }
}
