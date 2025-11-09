package com.example.busconnect.services.mappers;

import com.example.busconnect.api.dto.AssignmentDtos.AssignmentCreateRequest;
import com.example.busconnect.api.dto.AssignmentDtos.AssignmentResponse;
import com.example.busconnect.domine.entities.Assignment;
import com.example.busconnect.domine.entities.Route;
import com.example.busconnect.domine.entities.Trip;
import com.example.busconnect.domine.entities.User;
import com.example.busconnect.domine.entities.enums.TripStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AssignmentMapper Tests")
class AssignmentMapperTest {
    private AssignmentMapper assignmentMapper;

    @BeforeEach
    void setUp() {
        assignmentMapper = Mappers.getMapper(AssignmentMapper.class);
    }

    @Test
    @DisplayName("Debe mapear AssignmentCreateRequest a la entidad Assignment")
    void shouldMapCreateRequestToEntity() {
        AssignmentCreateRequest request = new AssignmentCreateRequest(
                1L,
                2L,
                3L
        );

        Assignment assignment = assignmentMapper.toEntity(request);

        assertNotNull(assignment);
        assertFalse(assignment.getChecklistOk());
        assertNull(assignment.getId());
    }

    @Test
    @DisplayName("Debe mapear la entidad Assignment a AssignmentResponse")
    void shouldMapEntityToResponse() {
        Route route = Route.builder().id(1L)
                .origin("Bogotá")
                .destination("Tunja")
                .build();

        Trip trip = Trip.builder().id(1L)
                .date(LocalDate.of(2025, 12, 25))
                .departureAt(LocalDateTime.of(2025, 12, 25, 8, 0))
                .status(TripStatus.SCHEDULED)
                .route(route)
                .build();

        User driver = User.builder().id(2L).username("Driver One").build();
        User dispatcher = User.builder().id(3L).username("Dispatcher One").build();
        LocalDateTime assignedAt = LocalDateTime.now();

        Assignment assignment = Assignment.builder()
                .id(1L)
                .trip(trip)
                .driver(driver)
                .dispatcher(dispatcher)
                .checklistOk(true)
                .assignedAt(assignedAt)
                .build();

        AssignmentResponse response = assignmentMapper.toResponse(assignment);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertTrue(response.checklistOk());
        assertEquals(assignedAt, response.assignedAt());
        assertEquals(1L, response.tripId());
        assertNotNull(response.tripInfo());
        assertEquals("SCHEDULED", response.tripStatus());
        assertEquals(2L, response.driverId());
        assertEquals("Driver One", response.driverName());
        assertEquals(3L, response.dispatcherId());
        assertEquals("Dispatcher One", response.dispatcherName());
    }
}