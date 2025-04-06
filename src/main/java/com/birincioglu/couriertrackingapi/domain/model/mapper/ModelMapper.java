package com.birincioglu.couriertrackingapi.domain.model.mapper;

import com.birincioglu.couriertrackingapi.domain.entity.Courier;
import com.birincioglu.couriertrackingapi.domain.entity.CourierLocation;
import com.birincioglu.couriertrackingapi.domain.entity.GeoLocation;
import com.birincioglu.couriertrackingapi.domain.entity.Store;
import com.birincioglu.couriertrackingapi.domain.model.command.CourierLocationCommand;
import com.birincioglu.couriertrackingapi.domain.model.command.CreateCourierCommand;
import com.birincioglu.couriertrackingapi.domain.model.dto.CourierDTO;
import com.birincioglu.couriertrackingapi.domain.model.dto.GeoLocationDTO;
import com.birincioglu.couriertrackingapi.domain.model.dto.StoreDTO;
import com.birincioglu.couriertrackingapi.domain.model.dto.TravelDTO;
import com.birincioglu.couriertrackingapi.domain.model.vo.ReadStoreVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ModelMapper {

    @Mapping(target = "location.lat", source = "readStoreVO.lat")
    @Mapping(target = "location.lng", source = "readStoreVO.lng")
    Store readStoreVOToStore(ReadStoreVO readStoreVO);

    StoreDTO storeToStoreDTO(Store store);

    GeoLocation geoLocationDtoToGeoLocation(GeoLocationDTO currentLocation);

    @Mapping(target = "time", source = "command.time", qualifiedByName = "stringToLocalDateTime")
    CourierLocation courierLocationCommandToCourierLocation(CourierLocationCommand command);

    Courier createCourierCommandToCourier(CreateCourierCommand command);

    CourierDTO courierToCourierDTO(Courier courier);

    @Mapping(target = "totalDistance", source = "totalDistance", qualifiedByName = "formatDouble")
    TravelDTO generateTotalTravelDTO(Double totalDistance, String type);

    @Named("stringToLocalDateTime")
    static LocalDateTime stringToLocalDateTime(String time) {
        return LocalDateTime.parse(time).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    @Named("formatDouble")
    default double formatDouble(double value) {
        DecimalFormat df = new DecimalFormat("#");
        return Double.parseDouble(df.format(value));
    }

}