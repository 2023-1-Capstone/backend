package com.capstone.carbonlive.building.controller;

import com.capstone.carbonlive.building.service.BuildingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BuildingController {

    private final BuildingService buildingService;

    @GetMapping("/buildings")
    public void findBuildings() {

    }
}
