package com.proj.PujaParikrama.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class PandalMetroEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pandal_id", nullable = false)
    private PandalEntity pandal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metro_id", nullable = false)
    private MetroStationEntity metroStation;

    @Column(name = "distance_km")
    private Double distanceKm;

    @Column(name = "walking_time_minutes")
    private Integer walkingTimeMinutes;
}
