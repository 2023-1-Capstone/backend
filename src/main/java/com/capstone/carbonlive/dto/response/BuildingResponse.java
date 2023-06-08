package com.capstone.carbonlive.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuildingResponse {
    private Long id;
    private String name;
    private BigDecimal gasArea;
    private BigDecimal elecArea;
    private String gasDescription;
    private String elecDescription;
}
