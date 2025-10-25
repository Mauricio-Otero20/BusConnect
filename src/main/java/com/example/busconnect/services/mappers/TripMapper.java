package com.example.busconnect.services.mappers;
import com.example.busconnect.api.dto.TripDtos.*;
import com.example.busconnect.domine.entities.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TripMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", source = "date")
    @Mapping(target = "departureAt", source = "departureAt")
    @Mapping(target = "arrivalEta", source = "arrivalEta")
    @Mapping(target = "route", source = "routeId", qualifiedByName = "mapRoute")
    @Mapping(target = "bus", source = "busId", qualifiedByName = "mapBus")
    @Mapping(target = "status", expression = "java(com.example.busconnect.domine.entities.enums.TripStatus.SCHEDULED)")
    Trip toEntity(TripCreateRequest dto);

    @Mapping(target = "departureAt", source = "departureAt")
    @Mapping(target = "arrivalEta", source = "arrivalEta")
    @Mapping(target = "bus", source = "busId", qualifiedByName = "mapBus")
    @Mapping(target = "status", source = "status")
    void updateEntity(TripUpdateRequest dto, @MappingTarget Trip entity);

    
    @Mapping(target = "routeId", expression = "java(entity.getRoute() != null ? entity.getRoute().getId() : null)")
    @Mapping(target = "routeName", expression = "java(entity.getRoute() != null ? entity.getRoute().getName() : null)")
    @Mapping(target = "origin", expression = "java(entity.getRoute() != null ? entity.getRoute().getOrigin() : null)")
    @Mapping(target = "destination", expression = "java(entity.getRoute() != null ? entity.getRoute().getDestination() : null)")
    @Mapping(target = "busId", expression = "java(entity.getBus() != null ? entity.getBus().getId() : null)")
    @Mapping(target = "busPlate", expression = "java(entity.getBus() != null ? entity.getBus().getPlate() : null)")
    @Mapping(target = "capacity", expression = "java(entity.getBus() != null ? entity.getBus().getCapacity() : null)")
    TripResponse toResponse(Trip entity);

    @Named("mapRoute")
    default Route mapRoute(Long id) {
        if (id == null) return null;
        Route r = new Route();
        r.setId(id);
        return r;
    }

    @Named("mapBus")
    default Bus mapBus(Long id) {
        if (id == null) return null;
        Bus b = new Bus();
        b.setId(id);
        return b;
    }
}