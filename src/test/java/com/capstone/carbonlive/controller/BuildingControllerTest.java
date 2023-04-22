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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@AutoConfigureRestDocs
class BuildingControllerTest extends AbstractRestDocsTest {

    @Autowired BuildingRepository buildingRepository;

    @BeforeEach
    void beforeEach() {
        buildingRepository.save(Building.builder()
                .name("building1")
                .gasArea(new BigDecimal(1111.11))
                .elecArea(new BigDecimal(2222.22))
                .gasDescription("gas1")
                .elecDescription("electricity1")
                .build());
        buildingRepository.save(Building.builder()
                .name("building2")
                .gasArea(new BigDecimal(3333.33))
                .elecArea(new BigDecimal(4444.44))
                .gasDescription("gas2")
                .elecDescription("electricity2")
                .build());
    }

    @Test
    @DisplayName("건물 정보 받아오기")
    public void findBuildings() throws Exception {
        //when
        ResultActions result = this.mockMvc.perform(get("/api/buildings"));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].id").value(1L))
                .andExpect(jsonPath("$.result[0].name").value("building1"))
                .andExpect(jsonPath("$.result[0].gasArea").value(1111.11))
                .andExpect(jsonPath("$.result[0].elecArea").value(2222.22))
                .andExpect(jsonPath("$.result[0].gasDescription").value("gas1"))
                .andExpect(jsonPath("$.result[0].elecDescription").value("electricity1"))
                .andExpect(jsonPath("$.result[1].id").value(2L))
                .andExpect(jsonPath("$.result[1].name").value("building2"))
                .andExpect(jsonPath("$.result[1].gasArea").value(3333.33))
                .andExpect(jsonPath("$.result[1].elecArea").value(4444.44))
                .andExpect(jsonPath("$.result[1].gasDescription").value("gas2"))
                .andExpect(jsonPath("$.result[1].elecDescription").value("electricity2"));

        //docs
        result.andDo(print())
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("result").description("결과 반환"),
                                fieldWithPath("result[].id").description("건물 코드(id)"),
                                fieldWithPath("result[].name").description("건물 이름"),
                                fieldWithPath("result[].gasArea").description("해당 건물에 대한 연면적"),
                                fieldWithPath("result[].elecArea").description("해당 건물에 대한 연면적"),
                                fieldWithPath("result[].gasDescription").description("포함된 건물"),
                                fieldWithPath("result[].elecDescription").description("포함된 건물")
                        )
                ));
    }

}