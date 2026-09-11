package com.proj.PujaParikrama.mappers;

import com.proj.PujaParikrama.dto.NearbyPlaceDTO;

import java.util.Map;

public class NearbyPlaceMapper {

    public static NearbyPlaceDTO toNearbyPlaceDTO(Map<String, Object> element, double userLat, double userLon){
        double lat = ((Number) element.getOrDefault("lat", element.get("center"))) != null
                ? ((Number) element.getOrDefault("center", element.get("lat"))).doubleValue()
                : 0.0;

        // Handle both node (lat/lon directly) and way/relation (center.lat/center.lon)
        if (element.containsKey("center")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> center = (Map<String, Object>) element.get("center");
            lat = ((Number) center.get("lat")).doubleValue();
            double lon = ((Number) center.get("lon")).doubleValue();
            return NearbyPlaceDTO.builder()
                    .placeId(element.get("type") + "/" + element.get("id"))
                    .name(getTag(element, "name"))
                    .address(getTag(element, "addr:full"))
                    .latitude(lat)
                    .longitude(lon)
                    .distanceInKm(calculateHaversineDistance(userLat, userLon, lat, lon))
                    .walkingTimeMinutes((int) Math.round(calculateHaversineDistance(userLat, userLon, lat, lon) / 5.0 * 60))
                    .build();
        } else {
            double lon = ((Number) element.get("lon")).doubleValue();
            return NearbyPlaceDTO.builder()
                    .placeId(element.get("type") + "/" + element.get("id"))
                    .name(getTag(element, "name"))
                    .address(getTag(element, "addr:full"))
                    .latitude(((Number) element.get("lat")).doubleValue())
                    .longitude(lon)
                    .distanceInKm(calculateHaversineDistance(userLat, userLon, ((Number) element.get("lat")).doubleValue(), lon))
                    .walkingTimeMinutes(0) // will be recalculated
                    .build();
        }
    }

    private static String getTag(Map<String, Object> element, String key) {
        @SuppressWarnings("unchecked")
        Map<String, String> tags = (Map<String, String>) element.get("tags");
        if (tags != null) {
            String value = tags.get(key);
            if (value != null) return value;
        }
        // Try alternate address tags
        if ("addr:full".equals(key)) {
            String addr = getTag(element, "addr:street");
            String city = getTag(element, "addr:city");
            if (addr != null || city != null) {
                return (addr != null ? addr : "") + ", " + (city != null ? city : "");
            }
        }
        return "";
    }

    private static double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
