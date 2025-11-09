package com.example.busconnect.services.service;

import com.example.busconnect.api.dto.OverbookingDtos.*;
import java.util.List;

public interface OverbookingService {
    OverbookingResponse requestOverbooking(OverbookingCreateRequest request, Long userId);
    OverbookingResponse approveOverbooking(Long requestId, Long dispatcherId, OverbookingApproveRequest request);
    OverbookingResponse rejectOverbooking(Long requestId, Long dispatcherId, OverbookingRejectRequest request);
    List<OverbookingResponse> getPendingRequests();
    List<OverbookingResponse> getOverbookingRequestsByTrip(Long tripId);
    List<OverbookingResponse> getOverbookingRequestsByStatus(String status);
    OverbookingResponse getOverbookingRequestById(Long id);
    boolean canOverbook(Long tripId);
    double getCurrentOccupancyRate(Long tripId);
    void expirePendingRequests();
}