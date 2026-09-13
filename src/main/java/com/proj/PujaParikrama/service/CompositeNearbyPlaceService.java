package com.proj.PujaParikrama.service;

import com.proj.PujaParikrama.dto.NearbyPlaceDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompositeNearbyPlaceService implements NearbyPlaceService {

    private final GooglePlacesServiceImpl googlePlacesService;
    private final NearbyPlaceServiceImpl nearbyPlaceService;

    @Override
    public List<NearbyPlaceDTO> findNearbyPlaces(String type, double lat, double lon, double radiusKm) {
        // Try Google Places first
        NearbyPlaceResult googleResult = googlePlacesService.findNearbyPlaces(type, lat, lon, radiusKm);

        if (!googleResult.shouldFallback()) {
            log.debug("Google Places succeeded with {} results for {}", googleResult.getPlaces().size(), type);
            return googleResult.getPlaces();
        }

        // Log why we're falling back
        log.warn("Google Places fallback triggered for {}: {} - {}", type, googleResult.getErrorCode(), googleResult.getErrorMessage());

        // Fallback to Geoapify
        NearbyPlaceResult geoapifyResult = nearbyPlaceService.findNearbyPlaces(type, lat, lon, radiusKm);

        if (!geoapifyResult.shouldFallback()) {
            log.debug("Geoapify fallback succeeded with {} results for {}", geoapifyResult.getPlaces().size(), type);
            return geoapifyResult.getPlaces();
        }

        // Both failed - log and return empty
        log.error("Both Google Places and Geoapify failed for {}: Google=[{}], Geoapify=[{}]",
                type, googleResult.getErrorCode(), geoapifyResult.getErrorCode());
        return List.of();
    }
}