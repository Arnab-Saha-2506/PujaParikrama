package com.proj.PujaParikrama.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RouteWaypointDTO {
    private String type; //METRO or PUJA
    private String name;
    private String metroLine; //Only for metro
    private Double distanceFromPrevKm;
    private Integer walkingMinutes;
    private Double latitude;
    private Double longitude;
}
