package com.capstone.carbonlive.controller;

import com.capstone.carbonlive.dto.*;
import com.capstone.carbonlive.restdocs.AbstractRestDocsTest;
import com.capstone.carbonlive.service.CarbonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

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
        CarbonYearResponse response1 = new CarbonYearResponse(2019);
        IntStream.range(0, 12).forEach(i -> response1.getUsages()[i] = i);
        responseList.add(response1);
        CarbonYearResponse response2 = new CarbonYearResponse(2022);
        IntStream.range(0, 12).forEach(i -> response2.getUsages()[i] = i * 2);
        responseList.add(response2);

        UsageResult<CarbonYearResponse> result = new UsageResult<>(responseList);

        given(carbonService.getYearsUsages())
                .willReturn(result);

        ResultActions resultActions = this.mockMvc.perform(get(uri + "year"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].year").value(2019))
                .andExpect(jsonPath("$.result[1].year").value(2022));
        for (int i = 0; i < 12; i++){
                    resultActions.andExpect(jsonPath("$.result[0].usages[" + i + "]").value(i))
                            .andExpect(jsonPath("$.result[1].usages[" + i + "]").value(i * 2));
                }
                resultActions.andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("result").description("결과 반환"),
                                fieldWithPath("result[].year").description("년도"),
                                fieldWithPath("result[].usages[]").description("해당 년도의 월별 총 탄소 배출량")
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

    @Test
    @DisplayName("전체 탄소 배출량")
    void getAllUsages() throws Exception {
        UsagePredictionResponse[] usages1 = new UsagePredictionResponse[12];
        IntStream.range(0, 12).forEach(i -> usages1[i] = UsagePredictionResponse.builder()
                .data(i + 1)
                .build()
        );
        ArrayList<UsageResponse> list = new ArrayList<>();

        UsageResponse response1 = new UsageResponse();
        response1.setYear(2018);
        System.arraycopy(usages1, 0, response1.getUsages(), 0, 12);

        UsageResponse response2 = new UsageResponse();
        response2.setYear(2019);
        IntStream.range(0, 12).forEach(i -> response2.getUsages()[i] = UsagePredictionResponse.builder().prediction(i + 1).build());

        list.add(response1);
        list.add(response2);

        UsageWithNameResponse usageWithNameResponse = new UsageWithNameResponse();
        usageWithNameResponse.setName("본관");
        usageWithNameResponse.setUsagesList(list);
        UsageResult<UsageWithNameResponse> result = new UsageResult<>(new ArrayList<>());
        result.add(usageWithNameResponse);

        given(carbonService.getAllUsages())
                .willReturn(result);

        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println("result = " + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));

        ResultActions resultActions = mockMvc.perform(get(uri + "area"))
                .andExpect(jsonPath("$.result[0].name").value("본관"));
        for (int i = 0; i < 12; i++) {
            resultActions.andExpect(jsonPath("$.result[0].usagesList[0].usages[" + i + "].data").value(i + 1))
                    .andExpect(jsonPath("$.result[0].usagesList[1].usages[" + i + "].prediction").value(i + 1));
        }
        resultActions.andDo(
                document("{class-name}/{method-name}",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                        responseFields(
                            fieldWithPath("result").description("결과 반환"),
                            fieldWithPath("result[].name").description("건물명"),
                            fieldWithPath("result[].usagesList").description("사용량 데이터"),
                            fieldWithPath("result[].usagesList[].year").description("해당 사용량 데이터의 년도"),
                            fieldWithPath("result[].usagesList[].usages").description("월별 사용량(1월-12월)"),
                            fieldWithPath("result[].usagesList[].usages[].data").type(JsonFieldType.NUMBER)
                                .description("실 사용량. null이면 실 사용량 없음").optional(),
                            fieldWithPath("result[].usagesList[].usages[].prediction").type(JsonFieldType.NUMBER)
                                .description("예측 사용량. null이면 실 사용량이 있거나 예측 사용량이 없음").optional()
                        )
                )
        );

        then(carbonService).should(times(1)).getAllUsages();
    }
}
