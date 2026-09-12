package com.proj.PujaParikrama.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RouteResponseDTO {
    private List<RouteWaypointDTO> route;
    private Double totalDistanceKm;
    private Integer totalWalkingMinutes;
    private Integer totalEstimatedMinutes;
}
