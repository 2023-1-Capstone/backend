package com.capstone.carbonlive.service;

import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.entity.Water;
import com.capstone.carbonlive.repository.WaterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class WaterServiceTest {
    @Autowired
    private WaterService waterService;
    @Autowired
    private WaterRepository waterRepository;

    @BeforeEach
    void setUp(){
        IntStream.rangeClosed(1, 10).forEach(i -> {
            Water water = Water.builder()
                    .usages(i)
                    .recordedAt( LocalDate.of(i < 6 ? 2018 : 2019, i, 1) )
                    .build();
            waterRepository.save(water);
        });
    }

    @Test
    @DisplayName("모든 수도 사용량을 반환")
    void getAll(){
        // given
        int[] expectYear = {2018, 2019};
        int[][] expectUsages = { {1, 2, 3, 4, 5, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 6, 7, 8, 9, 10, 0, 0} };
        // when
        UsageResult<UsageResponse> ascResult = waterService.getAll();
        // then
        for (int i = 0; i < 2; i++) {
            assertThat(ascResult.getResult().get(i).getYear()).isEqualTo(expectYear[i]);
            for (int j = 0; j < 12; j++){
                assertThat(ascResult.getResult().get(i).getUsages()[j].getData()).isEqualTo(expectUsages[i][j]);
            }
        }

    }
}