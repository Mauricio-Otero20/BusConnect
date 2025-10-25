package com.example.busconnect.services.service;


import com.example.busconnect.api.dto.ParcelDtos.*;
import com.example.busconnect.domine.entities.enums.ParcelStatus;

import java.util.List;
public interface ParcelService {

    ParcelResponse createParcel(ParcelCreateRequest request);

    ParcelResponse updateParcel(Long id, ParcelUpdateRequest request);

    ParcelResponse getParcelById(Long id);

    ParcelResponse getParcelByCode(String code);

    List<ParcelResponse> getAllParcels();

    List<ParcelResponse> getParcelsByStatus(ParcelStatus status);

    List<ParcelResponse> getParcelsByTripId(Long tripId);

    List<ParcelResponse> getParcelsByPhone(String phone);

    void deleteParcel(Long id);

    ParcelResponse markAsInTransit(Long id, Long tripId);

    ParcelResponse markAsDelivered(Long id, String otp, String photoUrl);

    ParcelResponse markAsFailed(Long id, String reason);

    boolean validateOtp(Long parcelId, String otp);
}
