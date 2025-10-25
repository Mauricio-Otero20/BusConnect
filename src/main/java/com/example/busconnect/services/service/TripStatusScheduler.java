package com.example.busconnect.services.service;

import com.example.busconnect.domine.entities.Trip;
import com.example.busconnect.domine.entities.enums.TripStatus;
import com.example.busconnect.domine.repositories.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripStatusScheduler {

    private final TripRepository tripRepository;

    /**
     *  Se ejecuta cada 2 minutos para actualizar estados de viajes automáticamente
     * Expresión cron: "segundo minuto hora día mes día-semana"
     * "0 2 * * * *" = cada 2 minutos en el segundo 0
     */
    @Scheduled(cron = "0 */2 * * * *")
    @Transactional
    public void updateTripStatuses() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);

        log.debug(" Iniciando actualización automática de estados de viajes...");

        try {
            // Obtener viajes que podrían necesitar actualización
            List<Trip> tripsToCheck = tripRepository.findTripsNeedingStatusUpdate(sevenDaysAgo);

            if (tripsToCheck.isEmpty()) {
                log.debug(" No hay viajes que actualizar");
                return;
            }

            int updatedCount = 0;
            int totalChecked = tripsToCheck.size();

            for (Trip trip : tripsToCheck) {
                try {
                    TripStatus currentStatus = trip.getStatus();
                    TripStatus newStatus = calculateTripStatus(trip, now);

                    // Solo actualizar si el estado cambió
                    if (newStatus != currentStatus) {
                        trip.setStatus(newStatus);
                        tripRepository.save(trip);
                        updatedCount++;

                        log.info(" Trip {} actualizado: {} → {} (Ruta: {}, Fecha: {}, Salida: {})",
                                trip.getId(),
                                currentStatus,
                                newStatus,
                                trip.getRoute() != null ? trip.getRoute().getName() : "N/A",
                                trip.getDate(),
                                trip.getDepartureAt().toLocalTime()
                        );
                    }
                } catch (Exception e) {
                    log.error(" Error al procesar trip {}: {}", trip.getId(), e.getMessage());
                }
            }

            if (updatedCount > 0) {
                log.info(" Actualización completada: {} de {} viajes actualizados", updatedCount, totalChecked);
            } else {
                log.debug("Se revisaron {} viajes, ninguno necesitó actualización", totalChecked);
            }

        } catch (Exception e) {
            log.error(" Error en actualización automática de viajes: {}", e.getMessage(), e);
        }
    }

    /**
     *  Calcula el estado que debería tener un viaje según la hora actual
     */
    private TripStatus calculateTripStatus(Trip trip, LocalDateTime now) {
        LocalDateTime departureDateTime = trip.getDepartureAt();
        LocalDateTime arrivalDateTime = trip.getArrivalEta();

        // 1. Si ya llegó → ARRIVED
        if (now.isAfter(arrivalDateTime) || now.isEqual(arrivalDateTime)) {
            return TripStatus.ARRIVED;
        }

        // 2. Si ya salió pero no ha llegado → DEPARTED
        if (now.isAfter(departureDateTime)) {
            return TripStatus.DEPARTED;
        }

        // 3.  (10 minutos antes de salir) → BOARDING
        LocalDateTime boardingStartTime = departureDateTime.minusMinutes(10);
        if (now.isAfter(boardingStartTime) || now.isEqual(boardingStartTime)) {
            return TripStatus.BOARDING;
        }
        return TripStatus.SCHEDULED;
    }
}