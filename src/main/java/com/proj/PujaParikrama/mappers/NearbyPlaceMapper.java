package com.proj.PujaParikrama.mappers;

import com.proj.PujaParikrama.dto.NearbyPlaceDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NearbyPlaceMapper {

    /**
     * Geoapify returns GeoJSON Feature format:
     * {
     *   "properties": { "name": "...", "address": { "address_line1": "...", "city": "..." } },
     *   "geometry": { "coordinates": [longitude, latitude] }  // NOTE: lon, lat order
     * }
     */
    @SuppressWarnings("unchecked")
    public static NearbyPlaceDTO toNearbyPlaceDTO(Map<String, Object> feature, double userLat, double userLon) {
        try {
            Map<String, Object> properties = (Map<String, Object>) feature.get("properties");
            if (properties == null) return null;

            // Extract coordinates (GeoJSON format: [lon, lat])
            Map<String, Object> geometry = (Map<String, Object>) feature.get("geometry");
            if (geometry == null) return null;

            List<Double> coordinates = (List<Double>) geometry.get("coordinates");
            double lon = coordinates.get(0);  // longitude
            double lat = coordinates.get(1);  // latitude

            // Calculate distance
            double distanceKm = calculateHaversine(userLat, userLon, lat, lon);

            // Extract name
            String name = (String) properties.get("name");
            if (name == null || name.isEmpty()) {
                name = (String) properties.getOrDefault("display_name", "Unnamed Place");
            }

            // Extract address
            String address = extractAddress(properties);

            // Build placeId
            Object featureId = feature.get("id");
            Object placeId = properties.getOrDefault("place_id",
                    (featureId != null ? featureId.toString() : "geoapify_" + lat + "_" + lon));

            return NearbyPlaceDTO.builder()
                    .placeId(placeId.toString())
                    .name(name)
                    .address(address)
                    .latitude(lat)
                    .longitude(lon)
                    .distanceInKm(Math.round(distanceKm * 100.0) / 100.0)
                    .walkingTimeMinutes((int) Math.round(distanceKm / 5.0 * 60))
                    .build();

        } catch (Exception e) {
            // Log silently and return null (filtered out by caller)
            return null;
        }
    }

    private static String extractAddress(Map<String, Object> properties) {
        // Try formatted address first
        String addrLine1 = (String) properties.getOrDefault("name", "");

        Map<String, Object> address = (Map<String, Object>) properties.get("address");
        if (address != null) {
            List<String> parts = new ArrayList<>();
            if (address.get("address_line1") != null) parts.add((String) address.get("address_line1"));
            if (address.get("address_line2") != null) parts.add((String) address.get("address_line2"));
            if (address.get("city") != null) parts.add((String) address.get("city"));

            String fullAddress = String.join(", ", parts);
            return fullAddress.isEmpty() ? addrLine1 : fullAddress;
        }

        return addrLine1;
    }

    private static double calculateHaversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}