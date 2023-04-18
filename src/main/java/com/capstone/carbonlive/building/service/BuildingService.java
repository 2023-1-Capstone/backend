package com.capstone.carbonlive.building.service;

import com.capstone.carbonlive.building.Building;
import com.capstone.carbonlive.building.repository.BuildingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildingService {

    private final BuildingRepository buildingRepository;

    List<BuildingDto> findAll() {

    }
}
