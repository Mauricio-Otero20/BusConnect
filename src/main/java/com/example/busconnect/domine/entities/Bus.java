package com.example.busconnect.domine.entities;

import com.example.busconnect.domine.entities.enums.BusStatus;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Builder.Default;

@Entity
@Table(name = "buses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String plate;

    @Column(nullable = false)
    private Integer capacity;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> amenities = new HashMap<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BusStatus status = BusStatus.ACTIVE;

    @Default
    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats = new ArrayList<>();
}
