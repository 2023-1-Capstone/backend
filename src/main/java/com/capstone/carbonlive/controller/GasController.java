package com.capstone.carbonlive.controller;

import com.capstone.carbonlive.dto.SeasonResponse;
import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.dto.UsageWithNameResponse;
import com.capstone.carbonlive.service.GasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gas")
public class GasController {

    private final GasService gasService;

    @GetMapping("/{buildingCode}")
    public ResponseEntity<UsageResult<UsageResponse>> findGasByBuilding(@PathVariable("buildingCode") Long buildingCode) {
        UsageResult<UsageResponse> usageResult = gasService.findByBuilding(buildingCode);

        return ResponseEntity.ok(usageResult);
    }

    @GetMapping("/season")
    public ResponseEntity<UsageResult<SeasonResponse>> findGasBySeason() {
        UsageResult<SeasonResponse> seasonResult = gasService.findBySeason();

        return ResponseEntity.ok(seasonResult);
    }

    @GetMapping("/area")
    public ResponseEntity<UsageResult<UsageWithNameResponse>> findGasAll(){
        UsageResult<UsageWithNameResponse> result = gasService.findAll();

        return ResponseEntity.ok(result);
    }
}
