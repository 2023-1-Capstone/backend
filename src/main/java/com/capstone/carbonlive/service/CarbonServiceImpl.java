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
        List<Carbon> carbonList = carbonRepository.findAll(Sort.by("recordedAt").ascending());
        int year = -1;
        int[] usages = new int[12];
        for (Carbon c : carbonList){
            if (year == -1)
                year = c.getRecordedAt().getYear();

            if (year < c.getRecordedAt().getYear()){
                insertYearResponse(result, year, usages);
                year = c.getRecordedAt().getYear();
            }

            int cIdx = c.getRecordedAt().getMonth().getValue() - 1;

            if (c.getUsages() != null)
                usages[cIdx] = c.getUsages();
        }
        if (year != -1){
            insertYearResponse(result, year, usages);
        }

        return result;
    }

    private static void insertYearResponse(UsageResult<CarbonYearResponse> result, int year, int[] usages) {
        int checkingIfUsagesSumIsZero = 0;
        CarbonYearResponse yearResponse = new CarbonYearResponse(year);
        for (int i = 0; i < 12; i++){
            yearResponse.getUsages()[i] = usages[i];
            checkingIfUsagesSumIsZero += usages[i];
            usages[i] = 0;
        }
        if (checkingIfUsagesSumIsZero == 0)
            return;
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
