package com.example.busconnect.api.controllers;

import com.example.busconnect.api.dto.AssignmentDtos.*;
import com.example.busconnect.security.config.CustomUserDetails;
import com.example.busconnect.services.service.AssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
@Slf4j
public class AssignmentController {

    private final AssignmentService assignmentService;

    // ==================== CRUD BÁSICO ====================

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('DISPATCHER', 'ADMIN')")
    public ResponseEntity<AssignmentResponse> createAssignment(
            @Valid @RequestBody AssignmentCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.info("Creating assignment for trip {} - driver: {} by user: {}",
                request.tripId(), request.driverId(), currentUser.getUsername());

        AssignmentResponse created = assignmentService.createAssignment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('DISPATCHER', 'ADMIN')")
    public ResponseEntity<List<AssignmentResponse>> getAllAssignments(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.debug("Retrieving all assignments by user: {}", currentUser.getUsername());

        List<AssignmentResponse> assignments = assignmentService.getAllAssignments();
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DISPATCHER', 'ADMIN')")
    public ResponseEntity<AssignmentResponse> getAssignmentById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.debug("Retrieving assignment: {} by user: {}", id, currentUser.getUsername());

        AssignmentResponse assignment = assignmentService.getAssignmentById(id);
        return ResponseEntity.ok(assignment);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('DISPATCHER', 'ADMIN')")
    public ResponseEntity<AssignmentResponse> updateAssignment(
            @PathVariable Long id,
            @Valid @RequestBody AssignmentUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.info("Updating assignment: {} by user: {}", id, currentUser.getUsername());

        if (currentUser.hasRole("DISPATCHER") && !currentUser.hasRole("ADMIN")) {
            AssignmentResponse existing = assignmentService.getAssignmentById(id);
            if (!existing.dispatcherId().equals(currentUser.getId())) {
                log.warn("Dispatcher {} attempted to update assignment {} created by another dispatcher",
                        currentUser.getId(), id);
                throw new AccessDeniedException("Solo puedes actualizar asignaciones que tú creaste");
            }
        }

        AssignmentResponse updated = assignmentService.updateAssignment(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAssignment(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.warn("Deleting assignment: {} by admin: {}", id, currentUser.getUsername());

        assignmentService.deleteAssignment(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== CONSULTAS ====================

    @GetMapping("/trip/{tripId}")
    @PreAuthorize("hasAnyRole('DISPATCHER', 'ADMIN')")
    public ResponseEntity<AssignmentResponse> getAssignmentByTrip(
            @PathVariable Long tripId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.debug("Retrieving assignment for trip: {} by user: {}", tripId, currentUser.getUsername());

        AssignmentResponse assignment = assignmentService.getAssignmentByTripId(tripId);
        return ResponseEntity.ok(assignment);
    }

    @GetMapping("/trip/{tripId}/details")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER', 'DRIVER')")
    public ResponseEntity<AssignmentResponse> getAssignmentWithDetails(
            @PathVariable Long tripId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.debug("Retrieving assignment with details for trip: {} by user: {}",
                tripId, currentUser.getUsername());

        AssignmentResponse assignment = assignmentService.getAssignmentWithDetails(tripId);

        if (currentUser.hasRole("DRIVER") && 
            !currentUser.hasRole("ADMIN") && 
            !currentUser.hasRole("DISPATCHER")) {
            
            if (!assignment.driverId().equals(currentUser.getId())) {
                log.warn("Driver {} attempted to access assignment for trip {} assigned to driver {}",
                        currentUser.getId(), tripId, assignment.driverId());
                throw new AccessDeniedException("No tienes permiso para ver esta asignación");
            }
        }

        return ResponseEntity.ok(assignment);
    }

    @GetMapping("/driver/{driverId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER', 'DRIVER')")
    public ResponseEntity<List<AssignmentResponse>> getAssignmentsByDriver(
            @PathVariable Long driverId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.debug("Retrieving assignments for driver: {} by user: {}",
                driverId, currentUser.getUsername());

        if (currentUser.hasRole("DRIVER") && 
            !currentUser.hasRole("ADMIN") && 
            !currentUser.hasRole("DISPATCHER")) {
            
            if (!currentUser.getId().equals(driverId)) {
                log.warn("Driver {} attempted to access assignments of driver {}",
                        currentUser.getId(), driverId);
                throw new AccessDeniedException("Solo puedes ver tus propias asignaciones");
            }
        }

        List<AssignmentResponse> assignments = assignmentService.getAssignmentsByDriverId(driverId);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/driver/{driverId}/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER', 'DRIVER')")
    public ResponseEntity<List<AssignmentResponse>> getActiveAssignmentsByDriver(
            @PathVariable Long driverId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.debug("Retrieving active assignments for driver: {} by user: {}",
                driverId, currentUser.getUsername());

        if (currentUser.hasRole("DRIVER") && 
            !currentUser.hasRole("ADMIN") && 
            !currentUser.hasRole("DISPATCHER")) {
            
            if (!currentUser.getId().equals(driverId)) {
                log.warn("Driver {} attempted to access active assignments of driver {}",
                        currentUser.getId(), driverId);
                throw new AccessDeniedException("Solo puedes ver tus propias asignaciones");
            }
        }

        List<AssignmentResponse> assignments = assignmentService.getActiveAssignmentsByDriver(driverId);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/driver/{driverId}/date")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER', 'DRIVER')")
    public ResponseEntity<List<AssignmentResponse>> getAssignmentsByDriverAndDate(
            @PathVariable Long driverId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.debug("Retrieving assignments for driver {} on date: {} by user: {}",
                driverId, date, currentUser.getUsername());

         if (currentUser.hasRole("DRIVER") && 
            !currentUser.hasRole("ADMIN") && 
            !currentUser.hasRole("DISPATCHER")) {
            
            if (!currentUser.getId().equals(driverId)) {
                log.warn("Driver {} attempted to access assignments of driver {} for date {}",
                        currentUser.getId(), driverId, date);
                throw new AccessDeniedException("Solo puedes ver tus propias asignaciones");
            }
        }

        List<AssignmentResponse> assignments = assignmentService.getAssignmentsByDriverAndDate(driverId, date);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/dispatcher/{dispatcherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<List<AssignmentResponse>> getAssignmentsByDispatcher(
            @PathVariable Long dispatcherId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.debug("Retrieving assignments created by dispatcher: {} by user: {}",
                dispatcherId, currentUser.getUsername());

        if (currentUser.hasRole("DISPATCHER") && !currentUser.hasRole("ADMIN")) {
            if (!currentUser.getId().equals(dispatcherId)) {
                log.warn("Dispatcher {} attempted to access assignments created by dispatcher {}",
                        currentUser.getId(), dispatcherId);
                throw new AccessDeniedException("Solo puedes ver las asignaciones que tú creaste");
            }
        }

        List<AssignmentResponse> assignments = assignmentService.getAssignmentsByDispatcherId(dispatcherId);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<List<AssignmentResponse>> getAssignmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.debug("Retrieving assignments between {} and {} by user: {}",
                start, end, currentUser.getUsername());

        List<AssignmentResponse> assignments = assignmentService.getAssignmentsByDateRange(start, end);
        return ResponseEntity.ok(assignments);
    }

    // ==================== ACCIONES ====================

    @PostMapping("/{id}/approve-checklist")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER', 'DRIVER')")
    public ResponseEntity<AssignmentResponse> approveChecklist(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.info("Approving checklist for assignment: {} by user: {}",
                id, currentUser.getUsername());

        AssignmentResponse assignment = assignmentService.getAssignmentById(id);

        if (currentUser.hasRole("DRIVER") && 
            !currentUser.hasRole("ADMIN") && 
            !currentUser.hasRole("DISPATCHER")) {
            
            if (!assignment.driverId().equals(currentUser.getId())) {
                log.warn("Driver {} attempted to approve checklist for assignment {} assigned to driver {}",
                        currentUser.getId(), id, assignment.driverId());
                throw new AccessDeniedException("Solo puedes aprobar tu propio checklist");
            }
        }

        AssignmentResponse updated = assignmentService.approveChecklist(id);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/trip/{tripId}/has-assignment")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> hasActiveAssignment(
            @PathVariable Long tripId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.debug("Checking if trip {} has active assignment by user: {}",
                tripId, currentUser.getUsername());

        boolean hasAssignment = assignmentService.hasActiveAssignment(tripId);
        return ResponseEntity.ok(hasAssignment);
    }
}