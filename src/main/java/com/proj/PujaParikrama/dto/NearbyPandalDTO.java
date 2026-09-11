package com.proj.PujaParikrama.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NearbyPandalDTO {
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;
    private Double distanceInKm;
    private Integer walkingTimeMinutes;
    private String nearbyMetroName;
}
