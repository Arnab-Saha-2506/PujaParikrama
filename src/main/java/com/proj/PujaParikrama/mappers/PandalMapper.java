package com.proj.PujaParikrama.mappers;

import com.proj.PujaParikrama.dto.DistanceResponseDTO;
import com.proj.PujaParikrama.dto.MetroStationResponseDTO;
import com.proj.PujaParikrama.dto.PandalDetailResponseDTO;
import com.proj.PujaParikrama.dto.PandalResponseDTO;
import com.proj.PujaParikrama.entity.AreaEntity;
import com.proj.PujaParikrama.entity.PandalEntity;
import com.proj.PujaParikrama.entity.PandalMetroEntity;
import com.proj.PujaParikrama.service.PandalService;
import com.proj.PujaParikrama.service.PandalServiceImpl;

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
                        .min(Comparator.comparing(pm -> {
                            Double dist = pm.getDistanceKm();
                            if(dist != null) return dist;
                            return calculateDistance(entity, pm);
                                }))
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

    private static double calculateDistance(PandalEntity pandal, PandalMetroEntity pm) {
        if (pandal.getLatitude() == null || pandal.getLongitude() == null) return Double.MAX_VALUE;
        if (pm.getMetroStation() == null ||
                pm.getMetroStation().getLatitude() == null ||
                pm.getMetroStation().getLongitude() == null) return Double.MAX_VALUE;

        return calculateHaversineDistance(
                pandal.getLatitude(), pandal.getLongitude(),
                pm.getMetroStation().getLatitude(),
                pm.getMetroStation().getLongitude()
        );
    }

    private static double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
