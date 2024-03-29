package com.capstone.carbonlive.controller;

import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.dto.response.WaterResponse;
import com.capstone.carbonlive.service.WaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/water")
public class WaterController {

    private final WaterService waterService;

    @GetMapping
    public ResponseEntity<UsageResult<WaterResponse>> getWater() {

        UsageResult<WaterResponse> result = waterService.getAll();

        return ResponseEntity.ok(result);
    }
}
