package com.capstone.carbonlive.controller;

import com.capstone.carbonlive.service.BuildingService;
import com.capstone.carbonlive.dto.BuildingResponse;
import com.capstone.carbonlive.dto.BuildingResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BuildingController {

    private final BuildingService buildingService;

    @GetMapping("/buildings")
    public ResponseEntity<BuildingResult> findBuildings() {
        List<BuildingResponse> result = buildingService.findAll();

        return ResponseEntity.ok(new BuildingResult(result));
    }
}
