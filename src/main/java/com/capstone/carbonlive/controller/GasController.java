package com.capstone.carbonlive.controller;

import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.service.GasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class GasController {

    private final GasService gasService;

    @GetMapping("/gas/{buildingCode}")
    public ResponseEntity<UsageResult<UsageResponse>> findGasByBuilding(@PathVariable("buildingCode") Long buildingCode) {
        UsageResult<UsageResponse> result = gasService.findByBuilding(buildingCode);

        return ResponseEntity.ok(result);
    }

}
