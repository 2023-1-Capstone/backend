package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.response.CarbonYearResponse;
import com.capstone.carbonlive.dto.response.UsagePredictionResponse;
import com.capstone.carbonlive.dto.response.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.dto.response.UsageWithNameResponse;
import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.entity.Carbon;
import com.capstone.carbonlive.entity.EntireCarbon;
import com.capstone.carbonlive.repository.BuildingRepository;
import com.capstone.carbonlive.repository.CarbonRepository;
import com.capstone.carbonlive.repository.EntireCarbonRepository;
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
    private final EntireCarbonRepository entireCarbonRepository;

    @Override
    public UsageResult<CarbonYearResponse> getYearsUsages() {
        UsageResult<CarbonYearResponse> result = new UsageResult<>(new ArrayList<>());
        List<EntireCarbon> carbonList = entireCarbonRepository.findAll(Sort.by("recordedAt").ascending());
        int year = -1;
        UsagePredictionResponse[] usages = new UsagePredictionResponse[12];
        for (EntireCarbon c : carbonList){
            if (year == -1)
                year = c.getRecordedAt().getYear();

            if (year < c.getRecordedAt().getYear()){
                insertYearResponse(result, year, usages);
                year = c.getRecordedAt().getYear();
            }

            int cIdx = c.getRecordedAt().getMonth().getValue() - 1;

            usages[cIdx] = new UsagePredictionResponse(c.getUsages(), c.getPrediction());
        }
        if (year != -1){
            insertYearResponse(result, year, usages);
        }

        return result;
    }

    private static void insertYearResponse(UsageResult<CarbonYearResponse> result, int year, UsagePredictionResponse[] usages) {
        CarbonYearResponse yearResponse = new CarbonYearResponse(year);
        for (int i = 0; i < 12; i++){
            yearResponse.getUsages()[i] = usages[i];
            usages[i] = null;
        }

        result.add(yearResponse);
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
