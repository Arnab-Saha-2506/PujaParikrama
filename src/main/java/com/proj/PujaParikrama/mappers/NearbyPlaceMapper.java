package com.proj.PujaParikrama.mappers;

import com.proj.PujaParikrama.dto.NearbyPlaceDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NearbyPlaceMapper {

    public static NearbyPlaceDTO toNearbyPlaceDTO(Map<String, Object> element, double userLat, double userLon){
        Double lat = null;
        Double lon = null;

        // Handle node type (direct lat/lon)
        if (element.containsKey("center")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> center = (Map<String, Object>) element.get("center");
            lat = ((Number) center.get("lat")).doubleValue();
            lon = ((Number) center.get("lon")).doubleValue();
        } else if (element.containsKey("lat") && element.containsKey("lon")) {
            lat = ((Number) element.get("lat")).doubleValue();
            lon = ((Number) element.get("lon")).doubleValue();
        } else {
            // Missing coordinates - skip this element
            return null;
        }

        double distance = calculateHaversineDistance(userLat, userLon, lat, lon);

        return NearbyPlaceDTO.builder()
                .placeId(element.get("type") + "/" + element.get("id"))
                .name(extractName(element))
                .address(extractAddress(element))
                .latitude(lat)
                .longitude(lon)
                .distanceInKm(Math.round(distance * 100.0) / 100.0)
                .walkingTimeMinutes((int) Math.round(distance / 5.0 * 60))
                .build();
    }

    private static String extractName(Map<String, Object> element) {
        Map<String, String> tags = getTags(element);
        if (tags != null && tags.containsKey("name")) {
            return tags.get("name");
        }
        return "Unnamed Place";
    }

    @SuppressWarnings("unchecked")
    private static String extractAddress(Map<String, Object> element) {
        Map<String, String> tags = getTags(element);
        if (tags == null) return "";

        // Try addr:full first, then construct from components
        if (tags.containsKey("addr:full")) return tags.get("addr:full");

        List<String> parts = new ArrayList<>();
        if (tags.containsKey("addr:street")) parts.add(tags.get("addr:street"));
        if (tags.containsKey("addr:housenumber")) parts.add(0, tags.get("addr:housenumber"));
        if (tags.containsKey("addr:city")) parts.add(tags.get("addr:city"));

        return String.join(", ", parts);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> getTags(Map<String, Object> element) {
        return (Map<String, String>) element.get("tags");
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
