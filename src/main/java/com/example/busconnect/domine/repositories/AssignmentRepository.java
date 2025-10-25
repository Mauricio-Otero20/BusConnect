package com.example.busconnect.domine.repositories;

import com.example.busconnect.domine.entities.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {


    @Query("""
        SELECT DISTINCT a FROM Assignment a
        LEFT JOIN FETCH a.trip t
        LEFT JOIN FETCH t.route r
        LEFT JOIN FETCH a.driver d
        LEFT JOIN FETCH a.dispatcher disp
        WHERE a.id = :id
        """)
    Optional<Assignment> findByIdWithDetails(@Param("id") Long id);


    Optional<Assignment> findByTripId(Long tripId);

    @Query("""
        SELECT DISTINCT a FROM Assignment a
        LEFT JOIN FETCH a.trip t
        LEFT JOIN FETCH t.route r
        LEFT JOIN FETCH a.driver d
        LEFT JOIN FETCH a.dispatcher disp
        WHERE a.trip.id = :tripId
        """)
    Optional<Assignment> findByTripIdWithDetails(@Param("tripId") Long tripId);

    boolean existsByTripId(Long tripId);

    List<Assignment> findByDriverId(Long driverId);

    @Query("""
        SELECT DISTINCT a FROM Assignment a
        LEFT JOIN FETCH a.trip t
        LEFT JOIN FETCH t.route r
        LEFT JOIN FETCH a.driver d
        LEFT JOIN FETCH a.dispatcher disp
        WHERE a.driver.id = :driverId 
        AND t.status IN ('SCHEDULED', 'BOARDING', 'DEPARTED')
        """)
    List<Assignment> findActiveAssignmentsByDriver(@Param("driverId") Long driverId);

    @Query("""
        SELECT DISTINCT a FROM Assignment a
        LEFT JOIN FETCH a.trip t
        LEFT JOIN FETCH t.route r
        LEFT JOIN FETCH a.driver d
        LEFT JOIN FETCH a.dispatcher disp
        WHERE a.driver.id = :driverId 
        AND a.assignedAt BETWEEN :start AND :end
        """)
    List<Assignment> findByDriverIdAndAssignedAtBetween(
            @Param("driverId") Long driverId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    
    List<Assignment> findByDispatcherId(Long dispatcherId);

    @Query("""
        SELECT DISTINCT a FROM Assignment a
        LEFT JOIN FETCH a.trip t
        LEFT JOIN FETCH t.route r
        LEFT JOIN FETCH a.driver d
        LEFT JOIN FETCH a.dispatcher disp
        WHERE t.departureAt BETWEEN :start AND :end
        """)
    List<Assignment> findByDepartureDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );


    @Query("""
        SELECT DISTINCT a FROM Assignment a
        LEFT JOIN FETCH a.trip t
        LEFT JOIN FETCH t.route r
        LEFT JOIN FETCH a.driver d
        LEFT JOIN FETCH a.dispatcher disp
        ORDER BY a.assignedAt DESC
        """)
    List<Assignment> findAllWithDetails();
}