package com.proj.PujaParikrama.dto;

import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MetroStationResponseDTO {
    private Long id;
    private String name;
    private String line;
    private Double latitude;
    private Double longitude;
    private Double distanceKm;
    private Integer walkingTimeMinutes;
}
