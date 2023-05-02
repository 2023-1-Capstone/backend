package com.capstone.carbonlive.controller;

import com.capstone.carbonlive.dto.CarbonYearResponse;
import com.capstone.carbonlive.dto.UsagePredictionResponse;
import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.restdocs.AbstractRestDocsTest;
import com.capstone.carbonlive.service.CarbonService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarbonController.class)
class CarbonControllerTest extends AbstractRestDocsTest {

    @MockBean
    private CarbonService carbonService;
    private final String uri = "/api/carbon/";

    @Test
    void getYearUsages() throws Exception {
        List<CarbonYearResponse> responseList = new ArrayList<>();
        responseList.add(new CarbonYearResponse(2019, 20190));
        responseList.add(new CarbonYearResponse(2022, 20220));

        UsageResult<CarbonYearResponse> result = new UsageResult<>(responseList);

        given(carbonService.getYearsUsages())
                .willReturn(result);

        this.mockMvc.perform(get(uri + "year"))
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

    @Test
    @DisplayName("건물별 탄소 배출량")
    public void getBuildingUsages() throws Exception {
        UsagePredictionResponse[] usages1 = new UsagePredictionResponse[12];
        IntStream.range(0, 12).forEach(i -> usages1[i] = UsagePredictionResponse.builder()
                .data(i + 1)
                .prediction(false)
                .build()
        );
        ArrayList<UsageResponse> list = new ArrayList<>();

        UsageResponse response1 = new UsageResponse();
        response1.setYear(2018);
        System.arraycopy(usages1, 0, response1.getUsages(), 0, 12);

        UsageResponse response2 = new UsageResponse();
        response2.setYear(2019);
        IntStream.range(0, 12).forEach(i -> response2.getUsages()[i] = UsagePredictionResponse.builder().data(i + 1).build());

        response2.getUsages()[11].setData(16);

        list.add(response1);
        list.add(response2);

        UsageResult<UsageResponse> result = new UsageResult<>(list);
        System.out.println("result = " + result);
        given(carbonService.getBuildingUsages(1L))
                .willReturn(result);

        mockMvc.perform(get(uri + "{buildingCode}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].year").value(2018))
                .andExpect(jsonPath("$.result[0].usages[11].data").value(12))
                .andExpect(jsonPath("$.result[1].year").value(2019))
                .andExpect(jsonPath("$.result[1].usages[11].data").value(16))
                .andDo(print())
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("buildingCode").description("건물 코드 (id)")),
                        responseFields(
                                fieldWithPath("result").description("결과 반환"),
                                fieldWithPath("result[].year").description("년도"),
                                fieldWithPath("result[].usages").description("해당 년도에 따른 월별 사용량 집합(1월-12월) + 예측값 여부"),
                                fieldWithPath("result[].usages[].data").description("사용량"),
                                fieldWithPath("result[].usages[].prediction").description("예측값: true, 실측값: false")
                        )
                ));

        then(carbonService).should(times(1)).getBuildingUsages(1L);

    }
}
