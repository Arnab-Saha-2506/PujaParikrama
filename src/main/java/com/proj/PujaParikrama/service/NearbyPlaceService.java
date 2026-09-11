package com.proj.PujaParikrama.service;

import com.proj.PujaParikrama.dto.NearbyPlaceDTO;

import java.util.List;

public interface NearbyPlaceService {
    List<NearbyPlaceDTO> findNearbyPlaces(String type, double lat, double lon, double radiusKm);
}
