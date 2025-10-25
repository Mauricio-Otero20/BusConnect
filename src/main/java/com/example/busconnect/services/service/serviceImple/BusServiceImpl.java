package com.example.busconnect.services.service.serviceImple;

import com.example.busconnect.api.dto.BusDtos.*;
import com.example.busconnect.domine.entities.Bus;
import com.example.busconnect.domine.entities.enums.BusStatus;
import com.example.busconnect.domine.repositories.BusRepository;
import com.example.busconnect.services.service.BusService;
import com.example.busconnect.services.mappers.BusMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BusServiceImpl implements BusService {

    private final BusRepository busRepository;
    private final BusMapper busMapper;

    @Override
    public BusResponse createBus(BusCreateRequest request) {
        if (busRepository.existsByPlate(request.plate())) {
            throw new IllegalArgumentException("La placa ya existe: " + request.plate());
        }

        Bus bus = busMapper.toEntity(request);
        Bus savedBus = busRepository.save(bus);
        return busMapper.toResponse(savedBus);
    }

    @Override
    public BusResponse updateBus(Long id, BusUpdateRequest request) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe el bus con ese ID: " + id));

        busMapper.updateEntity(request, bus);
        Bus updatedBus = busRepository.save(bus);
        return busMapper.toResponse(updatedBus);
    }

    @Override
    @Transactional
    public BusResponse getBusById(Long id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe el bus con ese ID: " + id));
        return busMapper.toResponse(bus);
    }

    @Override
    @Transactional
    public BusResponse getBusWithSeats(Long id) {
        Bus bus = busRepository.findByIdWithSeats(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe el bus con ese ID: " + id));
        return busMapper.toResponse(bus);
    }

    @Override
    @Transactional
    public BusResponse getBusbyPlate(String plate) {
        Bus bus = busRepository.findByPlate(plate)
                .orElseThrow(() -> new IllegalArgumentException("No existe el bus con esa placa: " + plate));
        return busMapper.toResponse(bus);
    }

    @Override
    @Transactional
    public List<BusResponse> getAllBuses() {
        return busRepository.findAll().stream()
                .map(busMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<BusResponse> getBusesByStatus(BusStatus status) {
        return busRepository.findByStatus(status).stream()
                .map(busMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<BusResponse> getAvailableBuses(Integer minCapacity) {
        return busRepository.findAvailableBusesByCapacity(BusStatus.ACTIVE, minCapacity).stream()
                .map(busMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBus(Long id) {
        if (!busRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe el bus con ese ID: " + id);
        }
        busRepository.deleteById(id);
    }

    @Override
    @Transactional
    public boolean existsByPlate(String plate) {
        return busRepository.existsByPlate(plate);
    }

    @Override
    public BusResponse changeBusStatus(Long id, BusStatus status) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe el bus con ese ID: " + id));
        bus.setStatus(status);
        Bus updatedBus = busRepository.save(bus);
        return busMapper.toResponse(updatedBus);
    }
}
