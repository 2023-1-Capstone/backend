package com.capstone.carbonlive.controller;

import com.capstone.carbonlive.dto.UsagePredictionResponse;
import com.capstone.carbonlive.dto.UsageResponse;
import com.capstone.carbonlive.dto.UsageResult;
import com.capstone.carbonlive.restdocs.AbstractRestDocsTest;
import com.capstone.carbonlive.service.WaterService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WaterController.class)
class WaterControllerTest extends AbstractRestDocsTest {
    @MockBean
    private WaterService waterService;
    private final String uri = "/api/water";

    @Test
    void getWater() throws Exception {
        List<UsageResponse> responses = new ArrayList<>();

        UsageResponse response1 = new UsageResponse(2018);
        IntStream.range(0, 12).forEach(i ->
                response1.getUsages()[i] = UsagePredictionResponse.builder().data(i + 1).build()
        );

        UsageResponse response2 = new UsageResponse(2019);
        IntStream.range(0, 9).forEach(i ->
                response2.getUsages()[i] = UsagePredictionResponse.builder().data(i + 1).build()
        );
        IntStream.range(8, 12).forEach(i ->
                response2.getUsages()[i] = UsagePredictionResponse.builder().data(i + 1).prediction(true).build()
        ); //2019.09 부터 예측값
        response2.getUsages()[11].setData(16);

        responses.add(response1);
        responses.add(response2);

        UsageResult<UsageResponse> usageResult = new UsageResult<>(responses);

        System.out.println("usageResult = " + usageResult);

        given(waterService.getAll())
                .willReturn(usageResult);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].year").value(2018))
                .andExpect(jsonPath("$.result[1].year").value(2019));

        for (int i = 0; i < 12; i++) {
            resultActions.andExpect(jsonPath("$.result[0].usages[" + i + "].data").value(i + 1));
        }

        resultActions
                .andExpect(jsonPath("$.result[1].usages[0].prediction").value(false))
                .andExpect(jsonPath("$.result[1].usages[8].prediction").value(true))
                .andExpect(jsonPath("$.result[1].usages[11].prediction").value(true))
                .andExpect(jsonPath("$.result[1].usages[11].data").value(16))
                .andDo(print())
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("result").description("결과 반환"),
                                fieldWithPath("result[].year").description("년도"),
                                fieldWithPath("result[].usages").description("해당 년도에 따른 월별 사용량 집합(1월-12월)"),
                                fieldWithPath("result[].usages[].data").description("사용량"),
                                fieldWithPath("result[].usages[].prediction").description("예측값: true, 실측값: false")
                        ))
                );

        then(waterService).should(times(1)).getAll();
    }
}
