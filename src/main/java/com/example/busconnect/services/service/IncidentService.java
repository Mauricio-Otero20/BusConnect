package com.example.busconnect.services.service;

import com.example.busconnect.api.dto.IncidentDtos.*;
import com.example.busconnect.domine.entities.enums.EntityType;
import com.example.busconnect.domine.entities.enums.IncidentType;

import java.time.LocalDateTime;
import java.util.List;
public interface IncidentService {

    IncidentResponse createIncident(IncidentCreateRequest request);

    IncidentResponse updateIncident(Long id, IncidentUpdateRequest request);

    IncidentResponse getIncidentById(Long id);

    List<IncidentResponse> getAllIncidents();

    List<IncidentResponse> getIncidentsByEntityTypeAndId(EntityType entityType, Long entityId);

    List<IncidentResponse> getIncidentsByType(IncidentType type);

    List<IncidentResponse> getIncidentsByReportedBy(Long reportedById);

    List<IncidentResponse> getIncidentsByDateRange(LocalDateTime start, LocalDateTime end);

    void deleteIncident(Long id);

    long countIncidentsByType(IncidentType type, LocalDateTime since);
}
