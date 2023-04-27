package com.capstone.carbonlive.controller;

import com.capstone.carbonlive.dto.CarbonYearResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.restdocs.AbstractRestDocsTest;
import com.capstone.carbonlive.service.CarbonService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarbonController.class)
class CarbonControllerTest extends AbstractRestDocsTest {
    @MockBean
    private CarbonService carbonService;
    private final String uri = "/api/carbon";
    @Test
    void getYearUsages() throws Exception {
        List<CarbonYearResponse> responseList = new ArrayList<>();
        responseList.add(new CarbonYearResponse(2019, 20190));
        responseList.add(new CarbonYearResponse(2022, 20220));

        UsageResult<CarbonYearResponse> result = new UsageResult<>(responseList);

        given(carbonService.getYearsUsages())
                .willReturn(result);

        this.mockMvc.perform(get(uri + "/year"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].year").value(2019))
                .andExpect(jsonPath("$.result[0].usages").value(20190))
                .andExpect(jsonPath("$.result[1].year").value(2022))
                .andExpect(jsonPath("$.result[1].usages").value(20220))
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("result").description("결과 반환"),
                                fieldWithPath("result[].year").description("년도"),
                                fieldWithPath("result[].usages").description("해당 년도의 총 탄소 배출량")
                        ))
                );

        then(carbonService).should(times(1)).getYearsUsages();
    }
}