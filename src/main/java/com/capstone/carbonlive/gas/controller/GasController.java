package com.capstone.carbonlive.gas.controller;

import com.capstone.carbonlive.gas.service.GasService;
import lombok.RequiredArgsConstructor;
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
    public void findGasOfBuilding(@PathVariable Long buildingCode) {

    }


}
