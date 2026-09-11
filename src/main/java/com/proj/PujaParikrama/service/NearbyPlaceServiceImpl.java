package com.proj.PujaParikrama.service;

import com.proj.PujaParikrama.dto.NearbyPlaceDTO;
import com.proj.PujaParikrama.mappers.NearbyPlaceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NearbyPlaceServiceImpl implements NearbyPlaceService{

    private final RestTemplate restTemplate;
    private static final String OVERPASS_URL = "https://overpass-api.de/api/interpreter";

    @Override
    @Cacheable(value = "nearbyPlaces", key = "{#type, #lat, #lon, #radiusKm}")
    public List<NearbyPlaceDTO> findNearbyPlaces(String type, double lat, double lon, double radiusKm) {
        int radiusMeters = (int) (radiusKm * 1000);

        String osmType = mapToOsmType(type);

        // Overpass QL query
        String query = String.format(
                "[out:json][timeout:25];" +
                        "(node[\"amenity\"=\"%s\"](around:%d,%.6f,%.6f);" +
                        " way[\"amenity\"=\"%s\"](around:%d,%.6f,%.6f);" +
                        " relation[\"amenity\"=\"%s\"](around:%d,%.6f,%.6f);" +
                        ");out center;",
                osmType, radiusMeters, lat, lon,
                osmType, radiusMeters, lat, lon,
                osmType, radiusMeters, lat, lon
        );

        // Build form-encoded request for Overpass API
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("data", query);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(OVERPASS_URL, requestEntity, Map.class);
        Map<String, Object> response = responseEntity.getBody();

        // Safely handle empty responses
        List<Map<String, Object>> elements = (response != null)
                ? (List<Map<String, Object>>) response.getOrDefault("elements", Collections.emptyList())
                : Collections.emptyList();

        return elements.stream()
                .limit(50)
                .map(result -> NearbyPlaceMapper.toNearbyPlaceDTO(result, lat, lon))
                .sorted(Comparator.comparing(NearbyPlaceDTO::getDistanceInKm))
                .toList();
    }

    private String mapToOsmType(String frontendType) {
        return switch (frontendType.toLowerCase()) {
            case "atm" -> "atm";
            case "police" -> "police";
            case "hospital" -> "hospital";
            case "restaurant" -> "restaurant";
            case "cafe" -> "cafe";
//            case "gas_station" -> "fuel";
            case "pharmacy" -> "pharmacy";
            default -> "atm";
        };
    }

}
