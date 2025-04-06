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
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreService {
    private final StoreRepository storeRepository;

    private final ObjectMapper objectMapper;
    private final ModelMapper mapper;

    @PostConstruct
    public void initStores() {
        if (storeRepository.count() == 0) {
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("stores.json")) {
                List<ReadStoreVO> readStores = objectMapper.readValue(inputStream, objectMapper.getTypeFactory().constructCollectionType(List.class, ReadStoreVO.class));
                List<Store> stores = readStores.stream().map(mapper::readStoreVOToStore).toList();
                storeRepository.saveAll(stores);
            } catch (Exception e) {
                log.error("Error save stores. error: {} ", e.getMessage());
            }
        }
    }

    public PageableDTO<StoreDTO> getStores(PageableQuery query) {
        Pageable pageable = PageRequest.of(query.getPage() - 1, query.getSize());
        Page<Store> storesPage = storeRepository.findAll(pageable);

        List<StoreDTO> storeDTOs = storesPage.getContent().stream()
                .map(mapper::storeToStoreDTO)
                .collect(Collectors.toList());

        return PageableDTO
                .<StoreDTO>builder()
                .totalElements(storesPage.getTotalElements())
                .totalPages(storesPage.getTotalPages())
                .page(query.getPage())
                .size(query.getSize())
                .content(storeDTOs)
                .build();
    }

    public StoreDTO findNearestStore(GeoLocation currentLocation) {

        return mapper.storeToStoreDTO(storeRepository.findAll().stream()
                .min(Comparator.comparingDouble(store -> currentLocation.distanceTo(store.getLocation())))
                .orElseThrow(() -> new NotFoundException(ErrorCodes.NOT_FOUND, String.format("Nearest store not found! Location %s", currentLocation))));
    }


}
