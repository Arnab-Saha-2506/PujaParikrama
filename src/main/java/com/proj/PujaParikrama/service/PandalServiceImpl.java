package com.proj.PujaParikrama.service;

import com.proj.PujaParikrama.dto.*;
import com.proj.PujaParikrama.entity.AreaEntity;
import com.proj.PujaParikrama.entity.PandalEntity;
import com.proj.PujaParikrama.entity.PandalMetroEntity;
import com.proj.PujaParikrama.exception.NotFoundException;
import com.proj.PujaParikrama.mappers.MetroMapper;
import com.proj.PujaParikrama.mappers.PandalMapper;
import com.proj.PujaParikrama.repository.AreaRepository;
import com.proj.PujaParikrama.repository.PandalMetroRepository;
import com.proj.PujaParikrama.repository.PandalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PandalServiceImpl implements PandalService{

    private final PandalRepository pandalRepository;
    private final AreaRepository areaRepository;
    private final PandalMetroRepository pandalMetroRepository;

    @Override
    public List<PandalResponseDTO> getPandalsByArea(Long areaId) {
        AreaEntity area;
        try{
            area = areaRepository.getReferenceById(areaId);
        }
        catch (Exception e){
            throw new NotFoundException("Area not found with id: "+areaId);
        }

        List<PandalEntity> pandalEntityList = pandalRepository.findByAreaId(areaId);

        return pandalEntityList.stream()
                .map(PandalMapper::toPandalResponseDTO)
                .toList();
    }

    @Override
    public PandalDetailResponseDTO getPandalDetails(Long pandalId) {
        PandalEntity pandal;

        try {
            pandal = pandalRepository.getReferenceById(pandalId);
        } catch (Exception e) {
            throw new NotFoundException("Pandal not found with id: "+pandalId);
        }

        AreaEntity area = pandal.getArea();

        List<PandalMetroEntity> pandalMetros = pandalMetroRepository.findByPandalIdWithMetroStation(pandalId);

        List<MetroStationResponseDTO> nearbyMetros = pandalMetros.stream()
                .map(MetroMapper::toMetroStationResponseDTO)
                .toList();

        return PandalMapper.toPandalDetailResponseDTO(pandal, area, nearbyMetros);

//        return null;
    }

    @Override
    public DistanceResponseDTO calculateDistance(Long pandalId, Double userLatitude, Double userLongitude) {

        if(userLatitude == null || userLongitude == null){
            throw new IllegalArgumentException("Latitude and Longitude are required");
        }
        if(userLatitude < -90 || userLatitude > 90){
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        if(userLongitude < -180 || userLongitude > 180){
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }

        PandalEntity pandal;

        try {
            pandal = pandalRepository.getReferenceById(pandalId);
        } catch (Exception e) {
            throw new NotFoundException("Pandal not found with id: "+pandalId);
        }

        if(pandal.getLatitude() == null || pandal.getLongitude() == null){
            throw new NotFoundException("Pandal location coordinates not available");
        }

        double distanceKm = calculateHaversineDistance(userLatitude, userLongitude, pandal.getLatitude(), pandal.getLongitude());

        int walkingMinutes = (int) Math.round((distanceKm / 5.0) * 60);

        return PandalMapper.toDistanceResponseDTO(pandal, distanceKm, walkingMinutes);

        //        return null;
    }

    @Override
    public List<NearbyPandalDTO> findNearbyPandals(double lat, double lon, double radiusKm) {
        List<PandalEntity> allPandals = pandalRepository.findAll();

        return allPandals.stream()
                .filter(p -> p.getLatitude() != null && p.getLongitude() != null)
                .map(p -> {
                    double distance = calculateHaversineDistance(lat, lon, p.getLatitude(), p.getLongitude());
                    return PandalMapper.toNearbyPandalDTO(p, distance);
                })
                .filter(dto -> dto.getDistanceInKm() <= radiusKm)
                .sorted(Comparator.comparing(NearbyPandalDTO::getDistanceInKm))
                .toList();
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2){
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2* Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
