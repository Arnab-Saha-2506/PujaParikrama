package com.proj.PujaParikrama.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NearbyPlaceDTO {
    private String placeId;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private Double distanceInKm;
    private Integer walkingTimeMinutes;
}
