package com.capstone.carbonlive.gas.controller;

import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.gas.service.GasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class GasController {

    private final GasService gasService;

    @GetMapping("/gas/{buildingCode}")
    public ResponseEntity findGasByBuilding(@PathVariable("buildingCode") Long buildingCode) {
        List<UsageResponse> result = gasService.findByBuilding(buildingCode);

        return ResponseEntity.ok(new UsageResult(result));
    }

}
