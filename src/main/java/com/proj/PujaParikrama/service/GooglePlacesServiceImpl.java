package com.proj.PujaParikrama.service;

import com.proj.PujaParikrama.dto.NearbyPlaceDTO;
import com.proj.PujaParikrama.mappers.NearbyPlaceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GooglePlacesServiceImpl {

    private final RestTemplate restTemplate;

    @Value("${GOOGLE_MAPS_API1}")
    private String apiKey;

    private static final String GOOGLE_PLACES_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    private static final int TIMEOUT_SECONDS = 5;

    private static final Map<String, String> GOOGLE_TYPE_MAP = Map.of(
            "atm", "atm",
            "hospital", "hospital",
            "pharmacy", "pharmacy",
            "restaurant", "restaurant",
            "cafe", "cafe",
            "police", "police",
            "toilet", "toilet"
    );

    public NearbyPlaceResult findNearbyPlaces(String type, double lat, double lon, double radiusKm) {
        String googleType = GOOGLE_TYPE_MAP.getOrDefault(type.toLowerCase(), type);
        int radiusMeters = (int) (radiusKm * 1000);

        String url = UriComponentsBuilder
                .fromUriString(GOOGLE_PLACES_URL)
                .queryParam("location", lat + "," + lon)
                .queryParam("radius", radiusMeters)
                .queryParam("type", googleType)
                .queryParam("key", apiKey)
                .toUriString();

        try {
            CompletableFuture<ResponseEntity<Map>> future = CompletableFuture.supplyAsync(
                    () -> restTemplate.getForEntity(url, Map.class)
            );

            ResponseEntity<Map> responseEntity = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            Map<String, Object> response = responseEntity.getBody();

            if (response == null) {
                return NearbyPlaceResult.failure("NULL_RESPONSE", "Google Places returned null response");
            }

            String status = (String) response.get("status");
            if (!"OK".equals(status)) {
                String errorCode = mapGoogleStatusToErrorCode(status);
                log.warn("Google Places API error [{}]: {}", errorCode, status);
                return NearbyPlaceResult.failure(errorCode, "Google Places API status: " + status);
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> results = (List<Map<String, Object>>) response.getOrDefault("results", Collections.emptyList());

            List<NearbyPlaceDTO> places = results.stream()
                    .limit(50)
                    .map(result -> NearbyPlaceMapper.toNearbyPlaceDTOFromGoogle(result, lat, lon))
                    .filter(Objects::nonNull)
                    .filter(place -> place.getDistanceInKm() <= radiusKm)
                    .sorted(Comparator.comparing(NearbyPlaceDTO::getDistanceInKm))
                    .collect(Collectors.toList());

            return NearbyPlaceResult.success(places);

        } catch (TimeoutException e) {
            log.warn("Google Places API timeout after {}s for type: {}", TIMEOUT_SECONDS, type);
            return NearbyPlaceResult.failure("TIMEOUT", "Request timed out after " + TIMEOUT_SECONDS + "s");
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.FORBIDDEN || e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return NearbyPlaceResult.failure("KEY_EXPIRED", "API key invalid or expired: " + e.getMessage());
            }
            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                return NearbyPlaceResult.failure("QUOTA_EXCEEDED", "Daily quota exceeded: " + e.getMessage());
            }
            log.error("Google Places HTTP error: {} - {}", e.getStatusCode(), e.getMessage());
            return NearbyPlaceResult.failure("HTTP_ERROR", "HTTP " + e.getStatusCode() + ": " + e.getMessage());
        } catch (HttpServerErrorException e) {
            log.error("Google Places server error: {} - {}", e.getStatusCode(), e.getMessage());
            return NearbyPlaceResult.failure("SERVER_ERROR", "Google server error: " + e.getMessage());
        } catch (ResourceAccessException e) {
            log.error("Google Places network error: {}", e.getMessage());
            return NearbyPlaceResult.failure("NETWORK_ERROR", "Network error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Google Places unexpected error for type {}: {}", type, e.getMessage());
            return NearbyPlaceResult.failure("UNKNOWN_ERROR", e.getMessage());
        }
    }

    private String mapGoogleStatusToErrorCode(String status) {
        return switch (status) {
            case "REQUEST_DENIED" -> "KEY_EXPIRED";
            case "OVER_QUERY_LIMIT" -> "QUOTA_EXCEEDED";
            case "INVALID_REQUEST" -> "INVALID_REQUEST";
            case "ZERO_RESULTS" -> "ZERO_RESULTS";
            default -> "API_ERROR";
        };
    }
}