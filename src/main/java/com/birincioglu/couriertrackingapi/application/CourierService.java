package com.birincioglu.couriertrackingapi.application;

import com.birincioglu.couriertrackingapi.domain.constant.ErrorCodes;
import com.birincioglu.couriertrackingapi.domain.entity.Courier;
import com.birincioglu.couriertrackingapi.domain.entity.GeoLocation;
import com.birincioglu.couriertrackingapi.domain.exception.NotFoundException;
import com.birincioglu.couriertrackingapi.domain.model.command.CourierLocationCommand;
import com.birincioglu.couriertrackingapi.domain.model.command.CreateCourierCommand;
import com.birincioglu.couriertrackingapi.domain.model.dto.CourierDTO;
import com.birincioglu.couriertrackingapi.domain.model.dto.StoreDTO;
import com.birincioglu.couriertrackingapi.domain.model.dto.TravelDTO;
import com.birincioglu.couriertrackingapi.domain.model.mapper.ModelMapper;
import com.birincioglu.couriertrackingapi.infrastructure.config.AppConfig;
import com.birincioglu.couriertrackingapi.infrastructure.persistence.CourierRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourierService {

    private static final String METER = "meter";
    private final CourierRepository courierRepository;
    private final StoreService storeService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ModelMapper mapper;
    private final AppConfig appConfig;


    @Transactional
    public void upsertCourierLocation(Long courierId, CourierLocationCommand command) {

        Courier courier = courierRepository.findByIdWithPessimisticWriteLock(courierId)
                .orElseThrow(() -> new NotFoundException(ErrorCodes.NOT_FOUND, String.format("Courier not found! courierId: %d ", courierId)));

        GeoLocation courierGeoLocation = mapper.geoLocationDtoToGeoLocation(command.getGeoLocation());

        StoreDTO nearestStore = storeService.findNearestStore(courierGeoLocation);

        double distanceToNearestStore = courierGeoLocation.distanceTo(nearestStore.getLocation());
        if (distanceToNearestStore < appConfig.getDistanceLimit()) {
            log.info("Nearest store found: {}, distance: {}m", nearestStore.getName(), distanceToNearestStore);

            String cacheKey = String.format("courier::%s::store::%s", courierId, nearestStore.getName());

            if (getFromRedis(cacheKey) != null) {
                log.info("[END] Courier location already saved for this store in last minute. courierId: {}, store: {}",
                        courierId, nearestStore.getName());
                return;
            }

            courier.addLocation(mapper.courierLocationCommandToCourierLocation(command));

            courierRepository.save(courier);

            redisTemplate.opsForValue().set(cacheKey, "visited", 1, TimeUnit.MINUTES);

            log.info("[END] Courier location successfully saved. courierId: {}, store: {}",
                    courierId, nearestStore.getName());

        } else {
            log.info("[END] No store found within {}m distance. courierId: {}",
                    appConfig.getDistanceLimit(), courierId);
        }

    }

    @Transactional
    public CourierDTO save(CreateCourierCommand command) {
        Courier savedCourier = courierRepository.save(mapper.createCourierCommandToCourier(command));
        log.info("[END] Courier successfully created. courier : {}", savedCourier);
        return mapper.courierToCourierDTO(savedCourier);

    }


    public TravelDTO getTotalTravelDistance(Long courierId) {
        Double totalDistance = courierRepository.findById(courierId).map(Courier::getTotalDistance)
                .orElseThrow(() -> new NotFoundException(ErrorCodes.NOT_FOUND, String.format("Courier not found! courierId: %d ", courierId)));
        return mapper.generateTotalTravelDTO(totalDistance, METER);
    }

    public String getFromRedis(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }
}
