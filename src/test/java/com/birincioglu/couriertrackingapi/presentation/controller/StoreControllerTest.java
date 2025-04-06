package com.birincioglu.couriertrackingapi.presentation.controller;

import com.birincioglu.couriertrackingapi.application.StoreService;
import com.birincioglu.couriertrackingapi.domain.entity.GeoLocation;
import com.birincioglu.couriertrackingapi.domain.model.dto.PageableDTO;
import com.birincioglu.couriertrackingapi.domain.model.dto.StoreDTO;
import com.birincioglu.couriertrackingapi.domain.model.query.PageableQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StoreControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StoreService storeService;

    @InjectMocks
    private StoreController storeController;

    private static final String BASE_URL = "/api/v1/store";
    private static final Long STORE_ID = 1L;
    private static final String STORE_NAME = "Test Store";
    private static final double LATITUDE = 41.0082;
    private static final double LONGITUDE = 28.9784;

    private StoreDTO storeDTO;
    private PageableDTO<StoreDTO> pageableDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(storeController).build();

        storeDTO = StoreDTO.builder()
                .id(STORE_ID.toString())
                .name(STORE_NAME)
                .location(GeoLocation.builder()
                        .lat(LATITUDE)
                        .lng(LONGITUDE)
                        .build())
                .build();

        pageableDTO = PageableDTO.<StoreDTO>builder()
                .totalElements(1)
                .totalPages(1)
                .page(1)
                .size(10)
                .content(Collections.singletonList(storeDTO))
                .build();
    }

    @Test
    void getAllStores_shouldReturnSuccessResponse() throws Exception {
        // Given
        when(storeService.getStores(any(PageableQuery.class))).thenReturn(pageableDTO);

        // When & Then
        mockMvc.perform(get(BASE_URL)
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.content[0].id").value(STORE_ID.toString()))
                .andExpect(jsonPath("$.data.content[0].name").value(STORE_NAME))
                .andExpect(jsonPath("$.data.content[0].location.lat").value(LATITUDE))
                .andExpect(jsonPath("$.data.content[0].location.lng").value(LONGITUDE));
    }

    @Test
    void getAllStores_withInvalidPageParam_shouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get(BASE_URL)
                        .param("page", "-1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllStores_withInvalidSizeParam_shouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get(BASE_URL)
                        .param("page", "1")
                        .param("size", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllStores_withEmptyResult_shouldReturnEmptyContent() throws Exception {
        // Given
        PageableDTO<StoreDTO> emptyPageableDTO = PageableDTO.<StoreDTO>builder()
                .totalElements(0)
                .totalPages(0)
                .page(1)
                .size(10)
                .content(Collections.emptyList())
                .build();

        when(storeService.getStores(any(PageableQuery.class))).thenReturn(emptyPageableDTO);

        // When & Then
        mockMvc.perform(get(BASE_URL)
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(0))
                .andExpect(jsonPath("$.data.totalPages").value(0))
                .andExpect(jsonPath("$.data.content").isEmpty());
    }
} 