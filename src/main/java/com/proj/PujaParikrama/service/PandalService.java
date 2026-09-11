package com.proj.PujaParikrama.service;

import com.proj.PujaParikrama.dto.DistanceResponseDTO;
import com.proj.PujaParikrama.dto.NearbyPandalDTO;
import com.proj.PujaParikrama.dto.PandalDetailResponseDTO;
import com.proj.PujaParikrama.dto.PandalResponseDTO;

import java.util.List;

public interface PandalService {
    List<PandalResponseDTO> getPandalsByArea(Long areaId);
    PandalDetailResponseDTO getPandalDetails(Long pandalId);
    DistanceResponseDTO calculateDistance(Long pandalId, Double userLatitude, Double userLongitude);
    List<NearbyPandalDTO> findNearbyPandals(double lat, double lon, double radiusKm);
}
