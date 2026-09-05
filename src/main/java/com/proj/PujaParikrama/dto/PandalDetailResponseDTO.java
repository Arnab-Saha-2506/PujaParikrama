package com.proj.PujaParikrama.dto;

import jakarta.persistence.Entity;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PandalDetailResponseDTO {
    private Long id;
    private String name;
    private String address;
    private String description;
    private String imageUrl;
    private Double latitude;
    private Double longitude;
    private String bestTimeToVisit;
    private String areaName;
    private List<MetroStationResponseDTO> nearbyMetros;
}
