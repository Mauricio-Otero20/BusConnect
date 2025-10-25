package com.example.busconnect.domine.entities;

import com.example.busconnect.domine.entities.enums.HoldStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import lombok.Builder.Default;

@Entity
@Table(name = "seat_holds")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeatHold {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false, length = 10)
    private String seatNumber;

    @Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HoldStatus status = HoldStatus.HOLD;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt =  LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
