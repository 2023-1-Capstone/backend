package com.capstone.carbonlive.service;

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

import static com.capstone.carbonlive.service.common.GetUsageResult.getUsageResult;

@Service
@RequiredArgsConstructor
public class ElectricityServiceImpl implements ElectricityService {
    private final ElectricityRepository electricityRepository;
    private final BuildingRepository buildingRepository;

    @Override
    public UsageResult getEachAll(Long id) {
        Building building = buildingRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        List<Electricity> electricityList = electricityRepository.findAllByBuilding(building,
                Sort.by("recordedAt").ascending());

        return getUsageResult(electricityList);
    }
}