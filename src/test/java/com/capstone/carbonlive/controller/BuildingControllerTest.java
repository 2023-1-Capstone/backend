package com.capstone.carbonlive.controller;

import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.repository.BuildingRepository;
import com.capstone.carbonlive.restdocs.AbstractRestDocsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@AutoConfigureRestDocs
class BuildingControllerTest extends AbstractRestDocsTest {

    @Autowired MockMvc mockMvc;

    @Autowired BuildingController buildingController;
    @Autowired BuildingRepository buildingRepository;

    @BeforeEach
    void beforeEach() {
        buildingRepository.save(Building.builder()
                .name("building1")
                .gasArea(new BigDecimal(1111.11))
                .elecArea(new BigDecimal(2222.22))
                .gasDescription(null)
                .elecDescription(null)
                .build());
        buildingRepository.save(Building.builder()
                .name("building2")
                .gasArea(new BigDecimal(1111.11))
                .elecArea(new BigDecimal(2222.22))
                .gasDescription(null)
                .elecDescription(null)
                .build());
    }

    @Test
    @DisplayName("건물 정보 받아오기")
    public void findBuildings() throws Exception {

        this.mockMvc.perform(get("/api/buildings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].name").value("building1"))
                .andExpect(jsonPath("$.result[0].gasArea").value(1111.11))
                .andDo(print());
    }

}