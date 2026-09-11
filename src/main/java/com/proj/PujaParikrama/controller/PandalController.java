package com.proj.PujaParikrama.controller;

import com.proj.PujaParikrama.dto.*;
import com.proj.PujaParikrama.service.NearbyPlaceService;
import com.proj.PujaParikrama.service.PandalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PandalController {
    private final PandalService pandalService;
    private final NearbyPlaceService nearbyPlaceService;

    @GetMapping("/areas/{areaId}/pandals")
    public ResponseEntity<List<PandalResponseDTO>> getPandalsByArea(@PathVariable Long areaId) {
        List<PandalResponseDTO> pandals = pandalService.getPandalsByArea(areaId);
        return ResponseEntity.ok().body(pandals);
    }

    @GetMapping("/pandals/{pandalId}")
    public ResponseEntity<PandalDetailResponseDTO> getPandalDetails(@PathVariable Long pandalId){
        PandalDetailResponseDTO responseDTO = pandalService.getPandalDetails(pandalId);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/pandals/{pandalId}/distance")
    public ResponseEntity<DistanceResponseDTO> calculateDistance(
            @PathVariable Long pandalId,
            @RequestParam double lat,
            @RequestParam double lon
    ){
        DistanceResponseDTO responseDTO = pandalService.calculateDistance(pandalId, lat, lon);
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/pandals/nearby")
    public ResponseEntity<List<NearbyPandalDTO>> findNearbyPandals(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam(defaultValue = "2") Double radiusKm
    ){
        List<NearbyPandalDTO> response = pandalService.findNearbyPandals(lat, lon, radiusKm);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/nearby/places")
    public ResponseEntity<List<NearbyPlaceDTO>> findNearbyPlaces(
            @RequestParam String type,
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam(defaultValue = "2") Double radiusKm) {

        // Validate type - only allow specific types
        List<String> allowedTypes = List.of("atm", "police", "hospital", "restaurant", "cafe", "pharmacy");
        if (!allowedTypes.contains(type.toLowerCase())) {
            return ResponseEntity.badRequest().build();
        }

        List<NearbyPlaceDTO> places = nearbyPlaceService.findNearbyPlaces(type, lat, lon, radiusKm);
        return ResponseEntity.ok(places);
    }

}
