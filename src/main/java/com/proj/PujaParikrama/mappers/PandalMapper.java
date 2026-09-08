package com.proj.PujaParikrama.mappers;

import com.proj.PujaParikrama.dto.DistanceResponseDTO;
import com.proj.PujaParikrama.dto.MetroStationResponseDTO;
import com.proj.PujaParikrama.dto.PandalDetailResponseDTO;
import com.proj.PujaParikrama.dto.PandalResponseDTO;
import com.proj.PujaParikrama.entity.AreaEntity;
import com.proj.PujaParikrama.entity.PandalEntity;
import com.proj.PujaParikrama.entity.PandalMetroEntity;

import java.util.Comparator;
import java.util.List;

public class PandalMapper {

//    toPandalResponseDTO
//            toPandalDetailResponseDTO
//    toMetroStationResponseDTO

    public static PandalResponseDTO toPandalResponseDTO(PandalEntity entity){
        String nearestMetro = null;

        try{
            if(entity.getPandalMetros() != null && !entity.getPandalMetros().isEmpty()){
                PandalMetroEntity nearest = entity.getPandalMetros().stream()
                        .filter(pm -> pm.getDistanceKm() != null)
                        .min(Comparator.comparing(PandalMetroEntity::getDistanceKm))
                        .orElse(null);
                if(nearest != null && nearest.getMetroStation() != null){
                    nearestMetro = nearest.getMetroStation().getName();
                }
            }
        } catch (Exception e) {
            nearestMetro = null;
        }
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
                .nearbyMetroStationName(nearestMetro)
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

    public static DistanceResponseDTO toDistanceResponseDTO(PandalEntity pandal, double distanceKm, int walkingMinutes){
        return DistanceResponseDTO.builder()
                .pandalId(pandal.getId())
                .pandalName(pandal.getName())
//                .userLatitude(userLatitude)
//                .userLongitude(userLongitude)
                .distanceInKm(Math.round(distanceKm * 100.0) / 100.0)
                .walkingTimeMinutes(walkingMinutes)
                .build();
    }
}
