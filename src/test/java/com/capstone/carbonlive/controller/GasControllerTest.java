//package com.capstone.carbonlive.controller;
//
//import com.capstone.carbonlive.entity.Building;
//import com.capstone.carbonlive.entity.Gas;
//import com.capstone.carbonlive.repository.BuildingRepository;
//import com.capstone.carbonlive.repository.GasRepository;
//import com.capstone.carbonlive.restdocs.AbstractRestDocsTest;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
//import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
//import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
//import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
//import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@AutoConfigureMockMvc
//@Transactional
//class GasControllerTest extends AbstractRestDocsTest {
//
//    @Autowired GasRepository gasRepository;
//    @Autowired BuildingRepository buildingRepository;
//
//    @Test
//    @DisplayName("건물별 가스 호출")
//    public void findGasByBuilding() throws Exception {
//        //given
//        Building building = Building.builder()
//                .name("building1")
//                .gasArea(new BigDecimal(1111.11))
//                .elecArea(new BigDecimal(2222.22))
//                .gasDescription(null)
//                .elecDescription(null)
//                .build();
//        buildingRepository.save(building);
//
//        Gas gas = Gas.builder()
//                .id(1L)
//                .recordedAt(LocalDate.now())
//                .usages(111)
//                .building(building)
//                .build();
//        gasRepository.save(gas);
//
//        //when
//        ResultActions result =
//                this.mockMvc.perform(get("/api/gas/building/{buildingCode}", 1L));
//
//        //then
//        result.andExpect(status().isOk())
//                .andDo(document("findGasByBuilding-get",
//                        pathParameters(
//                                parameterWithName("buildingCode").description("Building Id")
//                        ),
//                        responseFields(
//                                fieldWithPath("[].year").description("year"),
//                                fieldWithPath("[].usage").description("usage")
//                        )
//                ));
//    }
//
//}