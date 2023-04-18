package com.capstone.carbonlive.building.service;

import com.capstone.carbonlive.building.Building;
import com.capstone.carbonlive.building.repository.BuildingRepository;
import com.capstone.carbonlive.dto.BuildingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuildingService {

    private final BuildingRepository buildingRepository;

    public List<BuildingResponse> findAll() {
        List<Building> result = buildingRepository.findAll();

        List<BuildingResponse> collect = (List) result.stream().map(b -> {
            return new BuildingResponse(b.getId(), b.getName(), b.getGasArea(),
                    b.getElecArea(), b.getGasDescription(), b.getElecDescription());
        }).collect(Collectors.toList());
        return collect;
    }
}
