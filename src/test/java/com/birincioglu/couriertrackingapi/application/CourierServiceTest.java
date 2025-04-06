package com.birincioglu.couriertrackingapi.application;

import com.birincioglu.couriertrackingapi.domain.constant.ErrorCodes;
import com.birincioglu.couriertrackingapi.domain.entity.Courier;
import com.birincioglu.couriertrackingapi.domain.entity.CourierLocation;
import com.birincioglu.couriertrackingapi.domain.entity.GeoLocation;
import com.birincioglu.couriertrackingapi.domain.exception.NotFoundException;
import com.birincioglu.couriertrackingapi.domain.model.command.CourierLocationCommand;
import com.birincioglu.couriertrackingapi.domain.model.command.CreateCourierCommand;
import com.birincioglu.couriertrackingapi.domain.model.dto.CourierDTO;
import com.birincioglu.couriertrackingapi.domain.model.dto.GeoLocationDTO;
import com.birincioglu.couriertrackingapi.domain.model.dto.StoreDTO;
import com.birincioglu.couriertrackingapi.domain.model.dto.TravelDTO;
import com.birincioglu.couriertrackingapi.domain.model.mapper.ModelMapper;
import com.birincioglu.couriertrackingapi.infrastructure.config.AppConfig;
import com.birincioglu.couriertrackingapi.infrastructure.persistence.CourierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourierServiceTest {

    @Mock
    private CourierRepository courierRepository;

    @Mock
    private StoreService storeService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private ModelMapper mapper;

    @Mock
    private AppConfig appConfig;

    @InjectMocks
    private CourierService courierService;

    private static final Long COURIER_ID = 1L;
    private static final String USERNAME = "test_courier";
    private static final double LATITUDE = 41.0082;
    private static final double LONGITUDE = 28.9784;
    private static final String TIME = "2024-04-05T14:30";

    private Courier courier;
    private CourierLocation courierLocation;
    private CourierLocationCommand courierLocationCommand;
    private CreateCourierCommand createCourierCommand;

    @BeforeEach
    void setUp() {
        courier = Courier.builder()
                .id(COURIER_ID)
                .username(USERNAME)
                .totalDistance(0.0)
                .build();

        courierLocation = CourierLocation.builder()
                .geoLocation(GeoLocation.builder()
                        .lat(LATITUDE)
                        .lng(LONGITUDE)
                        .build())
                .time(LocalDateTime.parse(TIME))
                .build();

        courierLocationCommand = CourierLocationCommand.builder()
                .geoLocation(GeoLocationDTO.builder()
                        .lat(LATITUDE)
                        .lng(LONGITUDE)
                        .build())
                .time(TIME)
                .build();

        createCourierCommand = CreateCourierCommand.builder()
                .username(USERNAME)
                .build();
    }

    @Test
    void save_shouldCreateNewCourier() {
        // Given
        CourierDTO courierDTO = CourierDTO.builder()
                .id(COURIER_ID.toString())
                .username(USERNAME)
                .build();

        when(mapper.createCourierCommandToCourier(any())).thenReturn(courier);
        when(courierRepository.save(any())).thenReturn(courier);
        when(mapper.courierToCourierDTO(any())).thenReturn(courierDTO);

        // When
        CourierDTO actual = courierService.save(createCourierCommand);

        // Then
        assertNotNull(actual);
        assertEquals(COURIER_ID.toString(), actual.getId());
        assertEquals(USERNAME, actual.getUsername());
        verify(courierRepository).save(any());
    }

    @Test
    void upsertCourierLocation_shouldUpdateLocationWhenStoreIsNear() {
        // Given
        StoreDTO storeDTO = StoreDTO.builder()
                .name("Test Store")
                .location(GeoLocation.builder()
                        .lat(LATITUDE + 0.0001)
                        .lng(LONGITUDE + 0.0001)
                        .build())
                .build();

        when(appConfig.getDistanceLimit()).thenReturn(100);
        when(courierRepository.findByIdWithPessimisticWriteLock(any())).thenReturn(Optional.of(courier));
        when(storeService.findNearestStore(any())).thenReturn(storeDTO);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null);
        when(mapper.geoLocationDtoToGeoLocation(any())).thenReturn(GeoLocation.builder()
                .lat(LATITUDE)
                .lng(LONGITUDE)
                .build());
        when(mapper.courierLocationCommandToCourierLocation(any())).thenReturn(courierLocation);

        // When
        courierService.upsertCourierLocation(COURIER_ID, courierLocationCommand);

        // Then
        verify(courierRepository).save(any());
        verify(valueOperations).set(anyString(), anyString(), eq(1L), eq(TimeUnit.MINUTES));
    }

    @Test
    void upsertCourierLocation_shouldThrowNotFoundExceptionWhenCourierNotFound() {
        // Given
        when(courierRepository.findByIdWithPessimisticWriteLock(any())).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> courierService.upsertCourierLocation(COURIER_ID, courierLocationCommand));
        assertEquals(ErrorCodes.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void getTotalTravelDistance_shouldReturnTravelDTO() {
        // Given
        courier.setTotalDistance(100.0);
        TravelDTO travelDTO = TravelDTO.builder()
                .totalDistance(100.0)
                .type("meter")
                .build();

        when(courierRepository.findById(any())).thenReturn(Optional.of(courier));
        when(mapper.generateTotalTravelDTO(anyDouble(), anyString())).thenReturn(travelDTO);

        // When
        TravelDTO actual = courierService.getTotalTravelDistance(COURIER_ID);

        // Then
        assertNotNull(actual);
        assertEquals(100.0, actual.getTotalDistance());
        assertEquals("meter", actual.getType());
    }

    @Test
    void getTotalTravelDistance_shouldThrowNotFoundExceptionWhenCourierNotFound() {
        // Given
        when(courierRepository.findById(any())).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> courierService.getTotalTravelDistance(COURIER_ID));
        assertEquals(ErrorCodes.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void upsertCourierLocation_shouldNotSaveLocationWhenAlreadySavedInLastMinute() {
        // Given
        StoreDTO expected = StoreDTO.builder()
                .name("Test Store")
                .location(GeoLocation.builder()
                        .lat(LATITUDE + 0.0001)
                        .lng(LONGITUDE + 0.0001)
                        .build())
                .build();

        String cacheKey = String.format("courier::%s::store::%s", COURIER_ID, expected.getName());

        when(appConfig.getDistanceLimit()).thenReturn(100);
        when(courierRepository.findByIdWithPessimisticWriteLock(any())).thenReturn(Optional.of(courier));
        when(storeService.findNearestStore(any())).thenReturn(expected);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(cacheKey)).thenReturn("visited");
        when(mapper.geoLocationDtoToGeoLocation(any())).thenReturn(GeoLocation.builder()
                .lat(LATITUDE)
                .lng(LONGITUDE)
                .build());

        // When
        courierService.upsertCourierLocation(COURIER_ID, courierLocationCommand);

        // Then
        verify(courierRepository, never()).save(any());
        verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
    }

    @Test
    void upsertCourierLocation_shouldSaveLocationWhenNotSavedInLastMinute() {
        // Given
        StoreDTO expected = StoreDTO.builder()
                .name("Test Store")
                .location(GeoLocation.builder()
                        .lat(LATITUDE + 0.0001)
                        .lng(LONGITUDE + 0.0001)
                        .build())
                .build();

        String cacheKey = String.format("courier::%s::store::%s", COURIER_ID, expected.getName());

        when(appConfig.getDistanceLimit()).thenReturn(100);
        when(courierRepository.findByIdWithPessimisticWriteLock(any())).thenReturn(Optional.of(courier));
        when(storeService.findNearestStore(any())).thenReturn(expected);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(cacheKey)).thenReturn(null);
        when(mapper.geoLocationDtoToGeoLocation(any())).thenReturn(GeoLocation.builder()
                .lat(LATITUDE)
                .lng(LONGITUDE)
                .build());
        when(mapper.courierLocationCommandToCourierLocation(any())).thenReturn(courierLocation);

        // When
        courierService.upsertCourierLocation(COURIER_ID, courierLocationCommand);

        // Then
        verify(courierRepository).save(any());
        verify(valueOperations).set(eq(cacheKey), eq("visited"), eq(1L), eq(TimeUnit.MINUTES));
    }
} 