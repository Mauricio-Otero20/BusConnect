package com.example.busconnect.services.service;
import com.example.busconnect.api.dto.SeatDtos.*;
import com.example.busconnect.domine.entities.enums.SeatType;

import java.util.List;
public interface SeatService {

    SeatResponse createSeat(SeatCreateRequest request);

    SeatResponse updateSeat(Long id, SeatUpdateRequest request);

    SeatResponse getSeatById(Long id);

    SeatResponse getSeatByBusAndNumber(Long busId, String number);

    List<SeatResponse> getAllSeats();

    List<SeatResponse> getSeatsByBusId(Long busId);

    List<SeatResponse> getSeatsByBusIdAndType(Long busId, SeatType type);

    void deleteSeat(Long id);

    long countSeatsByBus(Long busId);

    void validateSeatNumber(Long busId, String number);
}
