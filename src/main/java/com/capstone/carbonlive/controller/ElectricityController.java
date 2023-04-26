package com.capstone.carbonlive.controller;

import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.dto.UsageWithNameResponse;
import com.capstone.carbonlive.service.ElectricityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/area")
    @ResponseStatus(HttpStatus.OK)
    public UsageResult<UsageWithNameResponse> getElectricityAll(){
        UsageResult<UsageWithNameResponse> result = electricityService.getAll();
        return result;
    }
}