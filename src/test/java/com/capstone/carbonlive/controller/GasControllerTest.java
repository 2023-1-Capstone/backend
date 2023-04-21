package com.capstone.carbonlive.controller;

import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.entity.Gas;
import com.capstone.carbonlive.repository.BuildingRepository;
import com.capstone.carbonlive.repository.GasRepository;
import com.capstone.carbonlive.restdocs.AbstractRestDocsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class GasControllerTest extends AbstractRestDocsTest {

    @Autowired MockMvc mockMvc;

    @Autowired GasRepository gasRepository;
    @Autowired BuildingRepository buildingRepository;

    @BeforeEach
    void beforeEach() {
        Building building = Building.builder()
                .name("building1")
                .gasArea(new BigDecimal(1111.11))
                .elecArea(new BigDecimal(2222.22))
                .gasDescription(null)
                .elecDescription(null)
                .build();
        buildingRepository.save(building);
        gasRepository.save(Gas.builder()
                .recordedAt(LocalDate.of(2022, 01, 01))
                .usages(111)
                .building(building)
                .build());
        gasRepository.save(Gas.builder()
                .recordedAt(LocalDate.of(2022, 02, 01))
                .usages(222)
                .building(building)
                .build());
    }

    @Test
    @DisplayName("건물별 가스 사용량 받아오기")
    public void findGasByBuilding() throws Exception {

        this.mockMvc.perform(get("/api/gas/{buildingCode}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].year").value(2022))
                .andExpect(jsonPath("$.result[0].usages[0]").value(111))
                .andExpect(jsonPath("$.result[0].usages[1]").value(222))
                .andDo(print());
    }

}