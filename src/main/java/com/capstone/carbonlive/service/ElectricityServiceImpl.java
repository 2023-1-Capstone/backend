package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.SeasonResponse;
import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.entity.Electricity;
import com.capstone.carbonlive.repository.BuildingRepository;
import com.capstone.carbonlive.repository.ElectricityRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.capstone.carbonlive.service.common.GetUsageResult.getBuildingUsageResult;
import static com.capstone.carbonlive.service.common.GetUsageResult.getSeasonUsageResult;

@Service
@RequiredArgsConstructor
public class ElectricityServiceImpl implements ElectricityService {
    private final ElectricityRepository electricityRepository;
    private final BuildingRepository buildingRepository;

    /**
     * 건물별 전기 사용량
     */
    @Override
    public UsageResult<UsageResponse> getEachAll(Long id) {
        Building building = buildingRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        List<Electricity> electricityList = electricityRepository.findAllByBuilding(building,
                Sort.by("recordedAt").ascending());

        return getBuildingUsageResult(electricityList);
    }

    /**
     * 계절별 전기 사용량
     */
    @Override
    public UsageResult<SeasonResponse> getSeasonData() {
        List<Electricity> electricityList = electricityRepository.findAll(Sort.by("recordedAt").ascending());

        return getSeasonUsageResult(electricityList);
    }
}