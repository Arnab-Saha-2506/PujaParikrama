package com.proj.PujaParikrama.service;

import com.proj.PujaParikrama.dto.NearbyPlaceDTO;
import com.proj.PujaParikrama.mappers.NearbyPlaceMapper;
import lombok.RequiredArgsConstructor;
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
public class NearbyPlaceServiceImpl implements NearbyPlaceService {

    private final RestTemplate restTemplate;

    @Value("${GEOPIFY_API_KEY}")
    private String apiKey;

    private static final String GEOAPIFY_URL = "https://api.geoapify.com/v2/places";

    @Override
    @Cacheable(value = "nearbyPlaces", key = "{#type, #lat, #lon, #radiusKm}")
    public List<NearbyPlaceDTO> findNearbyPlaces(String type, double lat, double lon, double radiusKm) {
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

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> features = (response != null)
                    ? (List<Map<String, Object>>) response.getOrDefault("features", Collections.emptyList())
                    : Collections.emptyList();

            return features.stream()
                    .limit(50)
                    .map(result -> NearbyPlaceMapper.toNearbyPlaceDTO(result, lat, lon))
                    .filter(Objects::nonNull)
                    .filter(place -> place.getDistanceInKm() <= radiusKm)
                    .sorted(Comparator.comparing(NearbyPlaceDTO::getDistanceInKm))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("NearbyPlaces error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return Collections.emptyList();
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
//            case "fuel" -> "fuel";
            default -> "service.financial.atm";
        };
    }
}