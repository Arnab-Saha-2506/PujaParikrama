package com.proj.PujaParikrama.mappers;

import com.proj.PujaParikrama.dto.MetroStationResponseDTO;
import com.proj.PujaParikrama.entity.MetroStationEntity;
import com.proj.PujaParikrama.entity.PandalMetroEntity;

public class MetroMapper {
    public static MetroStationResponseDTO toMetroStationResponseDTO(PandalMetroEntity pandalMetros){
        return MetroStationResponseDTO.builder()
                .id(pandalMetros.getMetroStation().getId())
                .name(pandalMetros.getMetroStation().getName())
                .line(pandalMetros.getMetroStation().getLine())
                .latitude(pandalMetros.getMetroStation().getLatitude())
                .longitude(pandalMetros.getMetroStation().getLongitude())
                .distanceKm(pandalMetros.getDistanceKm())
                .walkingTimeMinutes(pandalMetros.getWalkingTimeMinutes())
                .build();
    }

    public static MetroStationResponseDTO toMetroStationResponseDTO(MetroStationEntity station){
        return MetroStationResponseDTO.builder()
                .id(station.getId())
                .name(station.getName())
                .line(station.getLine())
                .latitude(station.getLatitude())
                .longitude(station.getLongitude())
                .build();
    }
}
