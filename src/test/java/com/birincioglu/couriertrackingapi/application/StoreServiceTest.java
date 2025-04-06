package com.birincioglu.couriertrackingapi.application;

import com.birincioglu.couriertrackingapi.domain.constant.ErrorCodes;
import com.birincioglu.couriertrackingapi.domain.entity.GeoLocation;
import com.birincioglu.couriertrackingapi.domain.entity.Store;
import com.birincioglu.couriertrackingapi.domain.exception.NotFoundException;
import com.birincioglu.couriertrackingapi.domain.model.dto.PageableDTO;
import com.birincioglu.couriertrackingapi.domain.model.dto.StoreDTO;
import com.birincioglu.couriertrackingapi.domain.model.mapper.ModelMapper;
import com.birincioglu.couriertrackingapi.domain.model.query.PageableQuery;
import com.birincioglu.couriertrackingapi.domain.model.vo.ReadStoreVO;
import com.birincioglu.couriertrackingapi.infrastructure.persistence.StoreRepository;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private StoreService storeService;

    private static final Long STORE_ID = 1L;
    private static final String STORE_NAME = "Test Store";
    private static final double LATITUDE = 41.0082;
    private static final double LONGITUDE = 28.9784;

    private Store store;
    private StoreDTO storeDTO;

    @BeforeEach
    void setUp() {
        store = Store.builder()
                .id(STORE_ID)
                .name(STORE_NAME)
                .location(GeoLocation.builder()
                        .lat(LATITUDE)
                        .lng(LONGITUDE)
                        .build())
                .build();

        storeDTO = StoreDTO.builder()
                .id(STORE_ID.toString())
                .name(STORE_NAME)
                .location(store.getLocation())
                .build();
    }

    @Test
    void getStores_shouldReturnPageableDTO() {
        // Given
        PageableQuery query = PageableQuery.builder()
                .page(1)
                .size(10)
                .build();

        Pageable pageable = PageRequest.of(query.getPage() - 1, query.getSize());
        Page<Store> storePage = new PageImpl<>(Collections.singletonList(store));

        when(storeRepository.findAll(pageable)).thenReturn(storePage);
        when(mapper.storeToStoreDTO(any())).thenReturn(storeDTO);

        // When
        PageableDTO<StoreDTO> actual = storeService.getStores(query);

        // Then
        assertNotNull(actual);
        assertEquals(1, actual.getTotalElements());
        assertEquals(1, actual.getTotalPages());
        assertEquals(1, actual.getPage());
        assertEquals(10, actual.getSize());
        assertFalse(actual.getContent().isEmpty());
        assertEquals(storeDTO, actual.getContent().iterator().next());
    }

    @Test
    void findNearestStore_shouldReturnNearestStore() {
        // Given
        GeoLocation currentLocation = GeoLocation.builder()
                .lat(LATITUDE + 0.0001)
                .lng(LONGITUDE + 0.0001)
                .build();

        when(storeRepository.findAll()).thenReturn(Collections.singletonList(store));
        when(mapper.storeToStoreDTO(any())).thenReturn(storeDTO);

        // When
        StoreDTO actual = storeService.findNearestStore(currentLocation);

        // Then
        assertNotNull(actual);
        assertEquals(storeDTO, actual);
    }

    @Test
    void findNearestStore_shouldThrowNotFoundExceptionWhenNoStoreFound() {
        // Given
        GeoLocation currentLocation = GeoLocation.builder()
                .lat(LATITUDE)
                .lng(LONGITUDE)
                .build();

        when(storeRepository.findAll()).thenReturn(Collections.emptyList());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> storeService.findNearestStore(currentLocation));
        assertEquals(ErrorCodes.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void initStores_shouldSaveStoresWhenRepositoryIsEmpty() throws Exception {
        // Given
        List<ReadStoreVO> readStores = Collections.singletonList(ReadStoreVO.builder()
                .name(STORE_NAME)
                .lat(LATITUDE)
                .lng(LONGITUDE)
                .build());

        Store sut = Store.builder()
                .name(STORE_NAME)
                .location(GeoLocation.builder()
                        .lat(LATITUDE)
                        .lng(LONGITUDE)
                        .build())
                .build();

        JavaType javaType = new ObjectMapper().getTypeFactory().constructCollectionType(List.class, ReadStoreVO.class);

        when(storeRepository.count()).thenReturn(0L);
        when(objectMapper.getTypeFactory()).thenReturn(new ObjectMapper().getTypeFactory());
        when(objectMapper.readValue(any(InputStream.class), eq(javaType))).thenReturn(readStores);
        when(mapper.readStoreVOToStore(any())).thenReturn(sut);
        when(storeRepository.saveAll(any())).thenReturn(Collections.singletonList(sut));

        // When
        storeService.initStores();

        // Then
        verify(storeRepository).count();
        verify(objectMapper).getTypeFactory();
        verify(objectMapper).readValue(any(InputStream.class), eq(javaType));
        verify(mapper).readStoreVOToStore(any());
        verify(storeRepository).saveAll(any());
    }

    @Test
    void initStores_shouldNotSaveStoresWhenRepositoryIsNotEmpty() {
        // Given
        when(storeRepository.count()).thenReturn(1L);

        // When
        storeService.initStores();

        // Then
        verify(storeRepository).count();
        verify(storeRepository, never()).saveAll(any());
    }

    @Test
    void initStores_shouldLogErrorWhenExceptionOccurs() throws Exception {
        // Given
        JavaType javaType = new ObjectMapper().getTypeFactory().constructCollectionType(List.class, ReadStoreVO.class);

        when(storeRepository.count()).thenReturn(0L);
        when(objectMapper.getTypeFactory()).thenReturn(new ObjectMapper().getTypeFactory());
        when(objectMapper.readValue(any(InputStream.class), eq(javaType)))
                .thenThrow(new IOException("Test error"));

        // When
        storeService.initStores();

        // Then
        verify(storeRepository).count();
        verify(storeRepository, never()).saveAll(any());
        verify(objectMapper).getTypeFactory();
        verify(objectMapper).readValue(any(InputStream.class), eq(javaType));
    }
} 