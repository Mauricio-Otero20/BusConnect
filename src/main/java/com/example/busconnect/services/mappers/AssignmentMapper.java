package com.example.busconnect.services.mappers;

import com.example.busconnect.api.dto.AssignmentDtos.*;
import com.example.busconnect.domine.entities.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {

    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trip", source = "tripId", qualifiedByName = "mapTrip")
    @Mapping(target = "driver", source = "driverId", qualifiedByName = "mapUser")
    @Mapping(target = "dispatcher", source = "dispatcherId", qualifiedByName = "mapUser")
    @Mapping(target = "checklistOk", expression = "java(dto.checklistOk() != null ? dto.checklistOk() : false)")
    @Mapping(target = "assignedAt", ignore = true)
    Assignment toEntity(AssignmentCreateRequest dto);

  
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trip", ignore = true)
    @Mapping(target = "driver", ignore = true)
    @Mapping(target = "dispatcher", ignore = true)
    @Mapping(target = "assignedAt", ignore = true)
    @Mapping(target = "checklistOk", source = "checklistOk")
    void updateEntity(AssignmentUpdateRequest dto, @MappingTarget Assignment entity);


    @Mapping(target = "tripId", source = "trip.id")
    @Mapping(target = "tripInfo", source = "trip", qualifiedByName = "formatTripInfo")
    @Mapping(target = "tripStatus", source = "trip.status")
    @Mapping(target = "tripDate", source = "trip.date")
    @Mapping(target = "tripDepartureTime", source = "trip.departureAt")
    @Mapping(target = "routeInfo", source = "trip.route", qualifiedByName = "formatRouteInfo")
    @Mapping(target = "driverId", source = "driver.id")
    @Mapping(target = "driverName", source = "driver.username")
    @Mapping(target = "dispatcherId", source = "dispatcher.id")
    @Mapping(target = "dispatcherName", source = "dispatcher.username")
    AssignmentResponse toResponse(Assignment entity);

    
    @Named("mapTrip")
    default Trip mapTrip(Long id) {
        if (id == null) return null;
        Trip t = new Trip();
        t.setId(id);
        return t;
    }

    @Named("mapUser")
    default User mapUser(Long id) {
        if (id == null) return null;
        User u = new User();
        u.setId(id);
        return u;
    }

    @Named("formatTripInfo")
    default String formatTripInfo(Trip trip) {
        if (trip == null) return null;
        if (trip.getRoute() == null) return "Viaje #" + trip.getId();
        
        try {
            String origin = trip.getRoute().getOrigin() != null 
                ? trip.getRoute().getOrigin() 
                : "?";
            String destination = trip.getRoute().getDestination() != null 
                ? trip.getRoute().getDestination() 
                : "?";
            String date = trip.getDate() != null 
                ? trip.getDate().toString() 
                : "";
            String time = trip.getDepartureAt() != null 
                ? trip.getDepartureAt().toLocalTime().toString() 
                : "";
            
            return origin + " → " + destination + 
                   (date.isEmpty() ? "" : " - " + date) + 
                   (time.isEmpty() ? "" : " " + time);
        } catch (Exception e) {
            return "Viaje #" + trip.getId();
        }
    }

    @Named("formatRouteInfo")
    default String formatRouteInfo(Route route) {
        if (route == null) return null;
        
        String origin = route.getOrigin() != null ? route.getOrigin() : "?";
        String destination = route.getDestination() != null ? route.getDestination() : "?";
        
        return origin + " → " + destination;
    }
}