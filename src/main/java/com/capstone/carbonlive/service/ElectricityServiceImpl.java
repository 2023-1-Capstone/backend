package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.*;
import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.entity.Electricity;
import com.capstone.carbonlive.entity.ElectricityFee;
import com.capstone.carbonlive.repository.BuildingRepository;
import com.capstone.carbonlive.repository.ElectricityFeeRepository;
import com.capstone.carbonlive.repository.ElectricityRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.capstone.carbonlive.service.common.GetUsageResult.*;

@Service
@RequiredArgsConstructor
public class ElectricityServiceImpl implements ElectricityService {

    private final ElectricityRepository electricityRepository;
    private final BuildingRepository buildingRepository;
    private final ElectricityFeeRepository electricityFeeRepository;

    /**
     * 건물별 전기 사용량
     */
    @Override
    public UsageResult<UsageResponse> getEachAll(Long id) {
        Building building = buildingRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        return getBuildingUsageResult(electricityRepository.findAllByBuilding(building,
                Sort.by("recordedAt").ascending().
                        and(Sort.by("prediction").descending())));
    }

    /**
     * 계절별 전기 사용량
     */
    @Override
    public UsageResult<SeasonResponse> getSeasonData() {
        return getSeasonUsageResult(electricityRepository.findAll(Sort.by("recordedAt").ascending()));
    }

    @Override
    public UsageResult<UsageWithNameResponse> getAll() {
        UsageResult<UsageWithNameResponse> result = new UsageResult<>(new ArrayList<>());

        List<Building> buildings = buildingRepository.findAll();
        for (Building b : buildings){
            UsageWithNameResponse response = new UsageWithNameResponse();
            response.setName(b.getName());
            UsageResult<UsageResponse> usageResult = getEachAll(b.getId());
            response.setUsagesList(usageResult.getResult());
            result.add(response);
        }

        return result;
    }

    /**
     * 월별 단위 전기 사용요금
     */
    @Override
    public UsageResult<FeeResponse> getFee() {
        return getUsageFeeResult(electricityFeeRepository.findAll(Sort.by("recordedAt").ascending()));
    }
}