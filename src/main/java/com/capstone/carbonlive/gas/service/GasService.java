package com.capstone.carbonlive.gas.service;

import com.capstone.carbonlive.building.Building;
import com.capstone.carbonlive.building.repository.BuildingRepository;
import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.gas.Gas;
import com.capstone.carbonlive.gas.repository.GasRepository;
import com.capstone.carbonlive.utils.LocalDateToYear;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GasService {

    private final BuildingRepository buildingRepository;
    private final GasRepository gasRepository;

    public List<UsageResponse> findByBuilding(Long buildingId) {
        Building building = buildingRepository.findById(buildingId).orElseThrow(
                () -> new RuntimeException());
        List<Gas> result = gasRepository.findByBuilding(building);

        List<UsageResponse> collect = (List) result.stream().map(g -> {
            String year = LocalDateToYear.getYear(g.getRecordedAt());
            return new UsageResponse(year, g.getUsages());
        }).collect(Collectors.toList());
        return collect;
    }
}
