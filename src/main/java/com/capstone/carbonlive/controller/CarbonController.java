package com.capstone.carbonlive.controller;

import com.capstone.carbonlive.dto.CarbonYearResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.service.CarbonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
}
