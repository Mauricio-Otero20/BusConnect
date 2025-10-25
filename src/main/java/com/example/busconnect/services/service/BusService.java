package com.example.busconnect.services.service;

import com.example.busconnect.api.dto.BusDtos.*;
import com.example.busconnect.domine.entities.enums.BusStatus;

import java.util.List;
public interface BusService {

    BusResponse createBus(BusCreateRequest request);

    BusResponse updateBus(Long id, BusUpdateRequest request);

    BusResponse getBusById(Long id);

    BusResponse getBusWithSeats(Long id);

    BusResponse getBusbyPlate(String plate);

    List<BusResponse> getAllBuses();

    List<BusResponse> getBusesByStatus(BusStatus status);

    List<BusResponse> getAvailableBuses(Integer minCapacity);

    void deleteBus(Long id);

    boolean existsByPlate(String plate);

    BusResponse changeBusStatus(Long id, BusStatus status);
}
