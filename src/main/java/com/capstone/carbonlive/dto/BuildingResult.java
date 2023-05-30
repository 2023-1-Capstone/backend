package com.capstone.carbonlive.dto;

import com.capstone.carbonlive.dto.response.BuildingResponse;
import lombok.Data;

import java.util.List;

@Data
public class BuildingResult {
    private List<BuildingResponse> result;

    public BuildingResult(List<BuildingResponse> result) {
        this.result = result;
    }
}
