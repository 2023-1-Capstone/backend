package com.capstone.carbonlive.controller;

import com.capstone.carbonlive.dto.CarbonYearResponse;
import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.service.CarbonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carbon")
@RequiredArgsConstructor
public class CarbonController {
    private final CarbonService carbonService;

    @GetMapping("/year")
    @ResponseStatus(HttpStatus.OK)
    public UsageResult<CarbonYearResponse> getYearUsages(){
        return carbonService.getYearsUsages();
    }

    @GetMapping("/{buildingCode}")
    public ResponseEntity<UsageResult<UsageResponse>> getBuildingUsages(@PathVariable("buildingCode") Long buildingCode ) {
        return ResponseEntity.ok(carbonService.getBuildingUsages(buildingCode));
    }
}