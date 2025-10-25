package com.example.busconnect.services.service.serviceImple;

import com.example.busconnect.api.dto.SeatHoldDtos.*;
import com.example.busconnect.domine.entities.SeatHold;
import com.example.busconnect.domine.entities.Trip;
import com.example.busconnect.domine.entities.User;
import com.example.busconnect.domine.entities.enums.HoldStatus;
import com.example.busconnect.domine.repositories.SeatHoldRepository;
import com.example.busconnect.domine.repositories.TripRepository;
import com.example.busconnect.domine.repositories.UserRepository;
import com.example.busconnect.services.service.SeatHoldService;
import com.example.busconnect.services.mappers.SeatHoldMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatHoldServiceImpl implements SeatHoldService {
    private final SeatHoldRepository seatHoldRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final SeatHoldMapper seatHoldMapper;

    private static final int HOLD_DURATION_MINUTES = 10;

    @Override
    public SeatHoldResponse createSeatHold(SeatHoldCreateRequest request, Long userId) {
        Trip trip = tripRepository.findById(request.tripId())
                .orElseThrow(() -> new IllegalArgumentException("Trip not found: " + request.tripId()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (isSeatHeld(request.tripId(), request.seatNumber())) {
            throw new IllegalArgumentException("Seat " + request.seatNumber() + " is already held");
        }

        SeatHold seatHold = seatHoldMapper.toEntity(request);
        seatHold.setTrip(trip);
        seatHold.setUser(user);
        seatHold.setExpiresAt(LocalDateTime.now().plusMinutes(HOLD_DURATION_MINUTES));
        seatHold.setStatus(HoldStatus.HOLD);

        SeatHold savedHold = seatHoldRepository.save(seatHold);
        return seatHoldMapper.toResponse(savedHold);
    }

    @Override
    public SeatHoldResponse updateSeatHold(Long id, SeatHoldUpdateRequest request) {
        SeatHold seatHold = seatHoldRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SeatHold not found: " + id));

        seatHoldMapper.updateEntity(request, seatHold);
        SeatHold updatedHold = seatHoldRepository.save(seatHold);
        return seatHoldMapper.toResponse(updatedHold);
    }

    @Override
    @Transactional
    public SeatHoldResponse getSeatHoldById(Long id) {
        SeatHold seatHold = seatHoldRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SeatHold not found: " + id));
        return seatHoldMapper.toResponse(seatHold);
    }

    @Override
    @Transactional
    public List<SeatHoldResponse> getAllSeatHolds() {
        return seatHoldRepository.findAll().stream()
                .map(seatHoldMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<SeatHoldResponse> getSeatHoldsByTripId(Long tripId) {
        if (!tripRepository.existsById(tripId)) {
            throw new IllegalArgumentException("Trip not found: " + tripId);
        }
        return seatHoldRepository.findByTripIdAndStatus(tripId, HoldStatus.HOLD).stream()
                .map(seatHoldMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<SeatHoldResponse> getSeatHoldsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        return seatHoldRepository.findByUserIdAndStatus(userId, HoldStatus.HOLD).stream()
                .map(seatHoldMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<SeatHoldResponse> getActiveSeatHoldsByTrip(Long tripId) {
        if (!tripRepository.existsById(tripId)) {
            throw new IllegalArgumentException("Trip not found: " + tripId);
        }
        return seatHoldRepository.findByTripIdAndStatus(tripId, HoldStatus.HOLD).stream()
                .map(seatHoldMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSeatHold(Long id) {
        if (!seatHoldRepository.existsById(id)) {
            throw new IllegalArgumentException("SeatHold not found: " + id);
        }
        seatHoldRepository.deleteById(id);
    }

    @Override
    public void expireOldHolds() {
        List<SeatHold> expiredHolds = seatHoldRepository.findExpiredHolds(LocalDateTime.now());
        expiredHolds.forEach(hold -> {
            hold.setStatus(HoldStatus.EXPIRED);
            seatHoldRepository.save(hold);
        });
    }

    @Override
    @Transactional
    public boolean isSeatHeld(Long tripId, String seatNumber) {
        return seatHoldRepository.existsByTripIdAndSeatNumberAndStatus(
                tripId, seatNumber, HoldStatus.HOLD);
    }

    @Override
    public void releaseSeatHold(Long holdId) {
        SeatHold seatHold = seatHoldRepository.findById(holdId)
                .orElseThrow(() -> new IllegalArgumentException("SeatHold not found: " + holdId));

        if (seatHold.getStatus() != HoldStatus.HOLD) {
            throw new IllegalArgumentException("Can only release active seatHolds");
        }

        seatHold.setStatus(HoldStatus.EXPIRED);
        seatHoldRepository.save(seatHold);
    }

    @Override
    public void convertHoldToTicket(Long holdId) {
        SeatHold seatHold = seatHoldRepository.findById(holdId)
                .orElseThrow(() -> new IllegalArgumentException("SeatHold not found: " + holdId));

        if (seatHold.getStatus() != HoldStatus.HOLD) {
            throw new IllegalArgumentException("Can only convert active holds");
        }

        seatHold.setStatus(HoldStatus.CONVERTED);
        seatHoldRepository.save(seatHold);
    }
}
