package com.proj.PujaParikrama.service;

import com.proj.PujaParikrama.dto.RouteRequestDTO;
import com.proj.PujaParikrama.dto.RouteResponseDTO;
import com.proj.PujaParikrama.dto.RouteWaypointDTO;
import com.proj.PujaParikrama.entity.MetroStationEntity;
import com.proj.PujaParikrama.entity.PandalEntity;
import com.proj.PujaParikrama.entity.PandalMetroEntity;
import com.proj.PujaParikrama.repository.MetroStationRepository;
import com.proj.PujaParikrama.repository.PandalMetroRepository;
import com.proj.PujaParikrama.repository.PandalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {

    private final PandalRepository pandalRepository;
    private final MetroStationRepository metroStationRepository;
    private final PandalMetroRepository pandalMetroRepository;

    @Override
    public RouteResponseDTO generateRoute(RouteRequestDTO requestDTO) {
        // 1. Load all selected pandals
        List<PandalEntity> selectedPandals = requestDTO.getPandalIds().stream()
                .map(id -> pandalRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Pandal not found: " + id)))
                .toList();

        // 2. For each pandal, find nearest metro station
        Map<Long, MetroStationEntity> nearestMetro = new HashMap<>();
        Map<Long, Double> nearestDistance = new HashMap<>();

        for (PandalEntity pandal : selectedPandals) {
            List<PandalMetroEntity> metroLinks = pandalMetroRepository.findByPandalId(pandal.getId());
            if (!metroLinks.isEmpty()) {
                PandalMetroEntity best = metroLinks.stream()
                        .min(Comparator.comparing(pm -> {
                            Double dist = pm.getDistanceKm();
                            if (dist != null) return dist;
                            return calculateHaversine(pandal, pm.getMetroStation());
                        }))
                        .orElseThrow();
                nearestMetro.put(pandal.getId(), best.getMetroStation());
                nearestDistance.put(pandal.getId(), best.getDistanceKm() != null
                        ? best.getDistanceKm()
                        : calculateHaversine(pandal, best.getMetroStation()));
            }
        }

        // 3. Group pandals by metro station
        Map<MetroStationEntity, List<pandalGroup>> groups = new HashMap<>();
        for (PandalEntity pandal : selectedPandals) {
            MetroStationEntity metro = nearestMetro.get(pandal.getId());
            if (metro != null) {
                double distToMetro = nearestDistance.get(pandal.getId());
                groups.computeIfAbsent(metro, k -> new ArrayList<>())
                        .add(new pandalGroup(pandal, distToMetro));
            }
        }

        // 4. Order metro stations (nearest neighbor heuristic)
        List<MetroStationEntity> orderedStations = orderStationsByProximity(groups.keySet().stream().toList());

        // 5. Build route
        List<RouteWaypointDTO> waypoints = new ArrayList<>();
        double totalKm = 0;
        int totalMinutes = 0;
        MetroStationEntity prevStation = null;
        double prevLat = 0, prevLon = 0;

        for (int i = 0; i < orderedStations.size(); i++) {
            MetroStationEntity station = orderedStations.get(i);

            double distanceFromPrev = 0;
            int walkingMinsFromPrev = 0;

            if (i == 0) {
                // First metro station - no walk to get here
                distanceFromPrev = 0;
                walkingMinsFromPrev = 0;
            } else {
                // Walk from previous station to this station
                distanceFromPrev = calculateHaversine(prevLat, prevLon,
                        station.getLatitude(), station.getLongitude());
                walkingMinsFromPrev = (int) Math.round(distanceFromPrev / 5.0 * 60);
            }

            waypoints.add(RouteWaypointDTO.builder()
                    .type("METRO")
                    .name(station.getName())
                    .metroLine(station.getLine())
                    .distanceFromPrevKm(round(distanceFromPrev))
                    .walkingMinutes(walkingMinsFromPrev)
                    .latitude(station.getLatitude())
                    .longitude(station.getLongitude())
                    .build());

            totalKm += distanceFromPrev;
            totalMinutes += walkingMinsFromPrev;

            // 6. Visit all pandals near this station (sorted by distance)
            List<pandalGroup> pandalsAtStation = groups.getOrDefault(station, Collections.emptyList());
            pandalsAtStation.sort(Comparator.comparingDouble(g -> g.distanceToMetro));

            double stationLat = station.getLatitude() != null ? station.getLatitude() : 0;
            double stationLon = station.getLongitude() != null ? station.getLongitude() : 0;

            for (pandalGroup group : pandalsAtStation) {
                PandalEntity pandal = group.pandal;
                double distFromStation = group.distanceToMetro;

                if (distFromStation > 0) {
                    waypoints.add(RouteWaypointDTO.builder()
                            .type("PUJA")
                            .name(pandal.getName())
                            .distanceFromPrevKm(round(distFromStation))
                            .walkingMinutes((int) Math.round(distFromStation / 5.0 * 60))
                            .latitude(pandal.getLatitude())
                            .longitude(pandal.getLongitude())
                            .build());

                    totalKm += distFromStation;
                    totalMinutes += (int) Math.round(distFromStation / 5.0 * 60);
                }
            }

            prevStation = station;
            prevLat = stationLat;
            prevLon = stationLon;
        }

        return RouteResponseDTO.builder()
                .route(waypoints)
                .totalDistanceKm(round(totalKm))
                .totalWalkingMinutes(totalMinutes)
                .totalEstimatedMinutes(totalMinutes)
                .build();
    }

    private List<MetroStationEntity> orderStationsByProximity(List<MetroStationEntity> stations) {
        if (stations.isEmpty()) return Collections.emptyList();

        List<MetroStationEntity> ordered = new ArrayList<>();
        Set<Long> visited = new HashSet<>();

        // Start from first station
        MetroStationEntity current = stations.get(0);
        ordered.add(current);
        visited.add(current.getId());

        while (visited.size() < stations.size()) {
            MetroStationEntity nearest = null;
            double nearestDist = Double.MAX_VALUE;

            double curLat = current.getLatitude() != null ? current.getLatitude() : 0;
            double curLon = current.getLongitude() != null ? current.getLongitude() : 0;

            for (MetroStationEntity s : stations) {
                if (visited.contains(s.getId())) continue;
                double lat = s.getLatitude() != null ? s.getLatitude() : 0;
                double lon = s.getLongitude() != null ? s.getLongitude() : 0;
                double dist = calculateHaversine(curLat, curLon, lat, lon);
                if (dist < nearestDist) {
                    nearestDist = dist;
                    nearest = s;
                }
            }

            if (nearest != null) {
                ordered.add(nearest);
                visited.add(nearest.getId());
                current = nearest;
            }
        }

        return ordered;
    }

    private double calculateHaversine(PandalEntity pandal, MetroStationEntity metro) {
        if (pandal.getLatitude() == null || pandal.getLongitude() == null) return Double.MAX_VALUE;
        if (metro.getLatitude() == null || metro.getLongitude() == null) return Double.MAX_VALUE;
        return calculateHaversine(pandal.getLatitude(), pandal.getLongitude(),
                metro.getLatitude(), metro.getLongitude());
    }

    private double calculateHaversine(double lat1, double lon1, double lat2, double lon2) {
        if (lat1 == 0 || lat2 == 0) return Double.MAX_VALUE;
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    // Helper class to pair pandals with their distance to metro
    private static class pandalGroup {
        final PandalEntity pandal;
        final double distanceToMetro;
        pandalGroup(PandalEntity pandal, double distanceToMetro) {
            this.pandal = pandal;
            this.distanceToMetro = distanceToMetro;
        }
    }
}