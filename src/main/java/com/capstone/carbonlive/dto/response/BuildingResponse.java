package com.capstone.carbonlive.dto.response;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class BuildingResponse {
    private Long id;
    private String name;
    private BigDecimal gasArea;
    private BigDecimal elecArea;
    private String gasDescription;
    private String elecDescription;

    public BuildingResponse(Long id, String name, BigDecimal gasArea, BigDecimal elecArea, String gasDescription, String elecDescription) {
        this.id = id;
        this.name = name;
        this.gasArea = gasArea;
        this.elecArea = elecArea;
        this.gasDescription = gasDescription;
        this.elecDescription = elecDescription;
    }
}
