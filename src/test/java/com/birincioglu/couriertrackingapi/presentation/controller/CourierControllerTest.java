package com.birincioglu.couriertrackingapi.presentation.controller;

import com.birincioglu.couriertrackingapi.application.CourierService;
import com.birincioglu.couriertrackingapi.domain.exception.NotFoundException;
import com.birincioglu.couriertrackingapi.domain.model.command.CourierLocationCommand;
import com.birincioglu.couriertrackingapi.domain.model.command.CreateCourierCommand;
import com.birincioglu.couriertrackingapi.domain.model.dto.CourierDTO;
import com.birincioglu.couriertrackingapi.domain.model.dto.GeoLocationDTO;
import com.birincioglu.couriertrackingapi.domain.model.dto.TravelDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CourierControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CourierService courierService;

    @InjectMocks
    private CourierController courierController;

    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/api/v1/courier";
    private static final Long COURIER_ID = 1L;
    private static final String USERNAME = "test_courier";
    private static final double LATITUDE = 41.0082;
    private static final double LONGITUDE = 28.9784;
    private static final String TIME = "2024-04-05T14:30";

    private CreateCourierCommand createCourierCommand;
    private CourierLocationCommand courierLocationCommand;
    private CourierDTO courierDTO;
    private TravelDTO travelDTO;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(courierController)
                .setValidator(validator)
                .build();
        objectMapper = new ObjectMapper();

        createCourierCommand = CreateCourierCommand.builder()
                .username(USERNAME)
                .build();

        courierLocationCommand = CourierLocationCommand.builder()
                .geoLocation(GeoLocationDTO.builder()
                        .lat(LATITUDE)
                        .lng(LONGITUDE)
                        .build())
                .time(TIME)
                .build();

        courierDTO = CourierDTO.builder()
                .id(COURIER_ID.toString())
                .username(USERNAME)
                .build();

        travelDTO = TravelDTO.builder()
                .totalDistance(100.0)
                .type("meter")
                .build();
    }

    @Test
    void save_shouldCreateNewCourier() throws Exception {
        // Given
        when(courierService.save(any())).thenReturn(courierDTO);

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCourierCommand)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(COURIER_ID.toString()))
                .andExpect(jsonPath("$.data.username").value(USERNAME));

        verify(courierService).save(any());
    }

    @Test
    void save_withInvalidUsername_shouldReturnBadRequest() throws Exception {
        // Given
        CreateCourierCommand invalidCommand = CreateCourierCommand.builder()
                .username("") // Boş kullanıcı adı
                .build();

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCommand)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void upsertCourierLocation_shouldUpdateLocation() throws Exception {
        // When & Then
        mockMvc.perform(put(BASE_URL + "/{courierId}/location", COURIER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courierLocationCommand)))
                .andExpect(status().isNoContent());

        verify(courierService).upsertCourierLocation(eq(COURIER_ID), any());
    }

    @Test
    void getTotalTravelDistance_shouldReturnTravelDTO() throws Exception {
        // Given
        when(courierService.getTotalTravelDistance(any())).thenReturn(travelDTO);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/{courierId}", COURIER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalDistance").value(100.0))
                .andExpect(jsonPath("$.data.type").value("meter"));

        verify(courierService).getTotalTravelDistance(eq(COURIER_ID));
    }

    @Test
    void getTotalTravelDistance_withNonExistentCourier_shouldReturnNotFound() throws Exception {
        // Given
        when(courierService.getTotalTravelDistance(any())).thenThrow(new NotFoundException("Courier not found"));

        // When & Then
        mockMvc.perform(get(BASE_URL + "/{courierId}", 999L))
                .andExpect(status().isNotFound());
    }
} 