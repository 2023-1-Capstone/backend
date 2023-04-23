package com.capstone.carbonlive.controller;

import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.service.ElectricityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/electricity")
@RequiredArgsConstructor
public class ElectricityController {
    private final ElectricityService electricityService;
    @GetMapping("/{buildingCode}")
    public ResponseEntity<UsageResult<UsageResponse>> getElectricityEach(@PathVariable(name = "buildingCode") Long id){
        UsageResult<UsageResponse> usageResult = electricityService.getEachAll(id);
        return ResponseEntity.ok(usageResult);
    }
}