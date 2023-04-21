package com.capstone.carbonlive.controller;

import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.restdocs.AbstractRestDocsTest;
import com.capstone.carbonlive.service.ElectricityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.stream.IntStream;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ElectricityController.class)
class ElectricityControllerTest extends AbstractRestDocsTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ElectricityService electricityService;
    private final String uri = "/api/electricity/";
    @Test
    //@WithMockUser // 스프링 시큐리티 회피
    void getElectricityEach() throws Exception {
        int[] usages = IntStream.rangeClosed(1, 12).toArray();
        ArrayList<UsageResponse> list = new ArrayList<>();

        UsageResponse response1 = new UsageResponse();
        response1.setYear(2018);
        System.arraycopy(usages, 0, response1.getUsages(), 0, 12);

        UsageResponse response2 = new UsageResponse();
        response2.setYear(2019);
        System.arraycopy(usages, 0, response2.getUsages(), 0, 12);
        response2.getUsages()[11] = 16;

        list.add(response1);
        list.add(response2);

        UsageResult result = new UsageResult(list);
        System.out.println("result = " + result);
        given(electricityService.getEachAll(1L))
                .willReturn(result);

        mockMvc.perform(get(uri + "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].year").value(2018))
                .andExpect(jsonPath("$.result[0].usages[11]").value(12))
                .andExpect(jsonPath("$.result[1].year").value(2019))
                .andExpect(jsonPath("$.result[1].usages[11]").value(16));

        then(electricityService).should(times(1)).getEachAll(1L);
    }
}