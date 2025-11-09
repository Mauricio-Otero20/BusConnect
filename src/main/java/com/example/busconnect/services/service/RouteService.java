package com.example.busconnect.services.service;

import com.example.busconnect.api.dto.RouteDtos.*;
import com.example.busconnect.api.dto.StopDtos.StopResponse;

import java.util.List;
public interface RouteService {

    RouteResponse createRoute(RouteCreateRequest request);

    RouteResponse updateRoute(Long id, RouteUpdateRequest request);

    RouteResponse getRouteById(Long id);

    RouteResponse getRouteByCode(String code);

    RouteResponse getRouteWithStops(Long id);

    List<RouteResponse> getAllRoutes();

    List<RouteResponse> searchRoutes(String origin, String destination);

    List<StopResponse> getStopsByRoute(Long routeId);

    void deleteRoute(Long id);

    boolean existsByCode(String code);
}
