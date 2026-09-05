package com.proj.PujaParikrama.mappers;

import com.proj.PujaParikrama.dto.MetroStationResponseDTO;
import com.proj.PujaParikrama.dto.PandalDetailResponseDTO;
import com.proj.PujaParikrama.dto.PandalResponseDTO;
import com.proj.PujaParikrama.entity.AreaEntity;
import com.proj.PujaParikrama.entity.PandalEntity;
import com.proj.PujaParikrama.entity.PandalMetroEntity;

import java.util.List;

public class PandalMapper {

//    toPandalResponseDTO
//            toPandalDetailResponseDTO
//    toMetroStationResponseDTO

    public static PandalResponseDTO toPandalResponseDTO(PandalEntity entity){
        return PandalResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .address(entity.getAddress())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .areaId(entity.getArea().getId())
                .areaName(entity.getArea().getName())
                .build();
    }

    public static PandalDetailResponseDTO toPandalDetailResponseDTO(PandalEntity entity, AreaEntity area, List<MetroStationResponseDTO> nearbyMetros){
        return PandalDetailResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .address(entity.getAddress())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .bestTimeToVisit(entity.getBestTimeToVisit())
                .areaName(area.getName())
                .nearbyMetros(nearbyMetros)
                .build();
    }
}
