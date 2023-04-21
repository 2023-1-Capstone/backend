package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.BuildingResponse;
import com.capstone.carbonlive.entity.Building;
import com.capstone.carbonlive.repository.BuildingRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BuildingServiceTest {

    @Autowired BuildingService buildingService;
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
    @DisplayName("모든 건물 정보 출력")
    public void findAll() throws Exception {
        //given, when
        List<BuildingResponse> all = buildingService.findAll();

        //then
        assertThat(all.size()).isEqualTo(2);
        assertThat(all.get(0).getName()).isEqualTo("building1");
        assertThat(all.get(1).getName()).isEqualTo("building2");
    }

}