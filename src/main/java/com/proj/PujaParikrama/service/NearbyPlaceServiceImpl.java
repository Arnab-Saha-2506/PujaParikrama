package com.proj.PujaParikrama.service;

import com.proj.PujaParikrama.dto.NearbyPlaceDTO;
import com.proj.PujaParikrama.mappers.NearbyPlaceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NearbyPlaceServiceImpl {

    private final RestTemplate restTemplate;

    @Value("${GEOPIFY_API_KEY}")
    private String apiKey;

    private static final String GEOAPIFY_URL = "https://api.geoapify.com/v2/places";

    @Cacheable(value = "nearbyPlaces", key = "{#type, #lat, #lon, #radiusKm}")
    public NearbyPlaceResult findNearbyPlaces(String type, double lat, double lon, double radiusKm) {
        try {
            String geoapifyType = mapToGeoapifyType(type);
            int radiusMeters = (int) (radiusKm * 1000);

            String url = UriComponentsBuilder
                    .fromUriString(GEOAPIFY_URL)
                    .queryParam("categories", geoapifyType)
                    .queryParam("filter", "circle:" + lon + "," + lat + "," + radiusMeters)
                    .queryParam("bias", "proximity:" + lon + "," + lat)
                    .queryParam("apiKey", apiKey)
                    .queryParam("limit", 50)
                    .toUriString();

            ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> response = responseEntity.getBody();

            if (response == null) {
                return NearbyPlaceResult.failure("NULL_RESPONSE", "Geoapify returned null response");
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> features = (response != null)
                    ? (List<Map<String, Object>>) response.getOrDefault("features", Collections.emptyList())
                    : Collections.emptyList();

            List<NearbyPlaceDTO> places = features.stream()
                    .limit(50)
                    .map(result -> NearbyPlaceMapper.toNearbyPlaceDTO(result, lat, lon))
                    .filter(Objects::nonNull)
                    .filter(place -> place.getDistanceInKm() <= radiusKm)
                    .sorted(Comparator.comparing(NearbyPlaceDTO::getDistanceInKm))
                    .collect(Collectors.toList());

            return NearbyPlaceResult.success(places);

        } catch (Exception e) {
            log.error("Geoapify error for type {}: {}", type, e.getMessage());
            return NearbyPlaceResult.failure("ERROR", e.getMessage());
        }
    }

    private String mapToGeoapifyType(String frontendType) {
        return switch (frontendType.toLowerCase()) {
            case "atm" -> "service.financial.atm";
            case "police" -> "service.police";
            case "hospital" -> "healthcare.hospital";
            case "pharmacy" -> "healthcare.pharmacy";
            case "restaurant" -> "catering.restaurant";
            case "cafe" -> "catering.cafe";
            case "toilet" -> "amenity.toilet";
            default -> "service.financial.atm";
        };
    }
}