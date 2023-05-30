package com.capstone.carbonlive.controller;

import com.capstone.carbonlive.dto.*;
import com.capstone.carbonlive.dto.response.FeeResponse;
import com.capstone.carbonlive.dto.response.SeasonResponse;
import com.capstone.carbonlive.dto.response.UsageResponse;
import com.capstone.carbonlive.dto.response.UsageWithNameResponse;
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
        return ResponseEntity.ok(gasService.findByBuilding(buildingCode));
    }

    @GetMapping("/season")
    public ResponseEntity<UsageResult<SeasonResponse>> findGasBySeason() {
        return ResponseEntity.ok(gasService.findBySeason());
    }

    @GetMapping("/area")
    public ResponseEntity<UsageResult<UsageWithNameResponse>> findGasAll(){
        return ResponseEntity.ok(gasService.findAll());
    }

    @GetMapping("/fee")
    public ResponseEntity<UsageResult<FeeResponse>> findGasFee() {
        return ResponseEntity.ok(gasService.findFee());
    }
}
