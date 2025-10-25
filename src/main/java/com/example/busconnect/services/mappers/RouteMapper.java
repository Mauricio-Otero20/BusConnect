package com.example.busconnect.services.mappers;
import com.example.busconnect.api.dto.RouteDtos.*;
import com.example.busconnect.domine.entities.Route;
import com.example.busconnect.domine.entities.Stop;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RouteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stops", ignore = true)
    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "origin", source = "origin")
    @Mapping(target = "destination", source = "destination")
    @Mapping(target = "distanceKm", source = "distanceKm")
    @Mapping(target = "durationMin", source = "durationMin")
    Route toEntity(RouteCreateRequest dto);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "distanceKm", source = "distanceKm")
    @Mapping(target = "durationMin", source = "durationMin")
    void updateEntity(RouteUpdateRequest dto, @MappingTarget Route route);

    @Mapping(target = "stops", source = "stops", qualifiedByName = "mapStopsToSummary")
    RouteResponse toResponse(Route entity);

    @Named("mapStopsToSummary")
    default List<StopSummary> mapStopsToSummary(List<Stop> stops) {
        if (stops == null) return null;
        return stops.stream()
                .map(stop -> new StopSummary(
                        stop.getId(),
                        stop.getName(),
                        stop.getOrder(),
                        stop.getLat(),
                        stop.getLng(),
                        stop.getRoute().getId()
                ))
                .toList();
    }
}
