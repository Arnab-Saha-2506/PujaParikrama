package com.proj.PujaParikrama.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DistanceResponseDTO {
    private Long pandalId;
    private String pandalName;
//    private Double userLatitude;
//    private Double userLongitude;
    private Double distanceInKm;
    private Integer walkingTimeMinutes;
}
