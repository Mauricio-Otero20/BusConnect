package com.example.busconnect.domine.entities;

import com.example.busconnect.domine.entities.enums.DynamicPricingStatus;
import com.example.busconnect.domine.entities.enums.PassengerType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder.Default;

@Entity
@Table(name = "fare_rules")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FareRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Default
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> discounts = new HashMap<>();

    @Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private DynamicPricingStatus dynamicPricing = DynamicPricingStatus.OFF;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<PassengerType, BigDecimal> passengerDiscounts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_stop_id", nullable = false)
    private Stop fromStop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_stop_id", nullable = false)
    private Stop toStop;
}
