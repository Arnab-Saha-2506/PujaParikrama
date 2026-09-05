package com.proj.PujaParikrama.dto;

import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PandalResponseDTO {
    private Long id;
    private String name;
    private String address;
    private String description;
    private String imageUrl;
    private Double latitude;
    private Double longitude;
    private Long areaId;
    private String areaName;
}
