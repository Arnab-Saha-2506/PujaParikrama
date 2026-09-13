package com.proj.PujaParikrama.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proj.PujaParikrama.dto.ExternalPandalDTO;
import com.proj.PujaParikrama.dto.ExternalPandalResponseDTO;
import com.proj.PujaParikrama.entity.AreaEntity;
import com.proj.PujaParikrama.entity.PandalEntity;
import com.proj.PujaParikrama.repository.AreaRepository;
import com.proj.PujaParikrama.repository.PandalRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class PandalImportService {
    private final ResourceLoader resourceLoader;
    private final RestTemplate restTemplate;
    private final PandalRepository pandalRepository;
    private final AreaRepository areaRepository;
    private final ObjectMapper objectMapper;

    @Value("${PUJOS_JSON_URL}")
    private String pujaJsonUrl;

    @Value("${PUJOS_JSON_SOURCE:PUJOS_JSON}")
    private String dataSource;

    private static final Map<String, String> ZONE_TO_AREA = Map.of(
            "CCU-S", "South Kolkata",
            "CCU-N", "North Kolkata",
            "CCU-E", "East Kolkata",
            "CCU-C", "Central Kolkata",
            "HWH", "Howrah",
            "Baghbazar", "North Kolkata",
            "HOO", "Hoogly"
    );

    private Map<String, AreaEntity> areaCache;

    @PostConstruct
    public void init(){
        loadAreaCache();
    }

    private void loadAreaCache(){
        List<AreaEntity> areas = areaRepository.findAll();
        areaCache = areas.stream()
                .collect(Collectors.toMap(AreaEntity::getName, a -> a));
        log.info("Loaded {} areas for import mapping", areaCache.size());
    }

    @Transactional
    public ImportResult importPujoJson(){
        log.info("Starting PUJOS_JSON import (source: {})",pujaJsonUrl.isBlank() ? "classpath:PUJA.json" : pujaJsonUrl);
        try{
            ExternalPandalResponseDTO responseDTO = fetchExternalData();
            if(responseDTO == null || responseDTO.getResult() == null){
                return ImportResult.failure("Failed to fetch or parse external data");
            }

            List<ExternalPandalDTO> externalPandals = responseDTO.getResult();
            log.info("Fetched {} pandals from external source", externalPandals.size());

            int created = 0, updated = 0, skipped = 0, errors = 0;

            for(ExternalPandalDTO extPandal : externalPandals){
                try{
                    ImportStatus status = processExternalPandal(extPandal);
                    switch (status){
                        case CREATED -> created++;
                        case UPDATED -> updated++;
                        case SKIPPED -> skipped++;
                    }
                } catch (Exception e) {
                    errors++;
                    log.error("Error processing pandal {}: {}", extPandal.getId(), e.getMessage());
                }
            }
            log.info("Import completed: created={}, updated={}, skipped={}, errors={}",
                    created, updated, skipped, errors);
            return ImportResult.success(created, updated, skipped, errors);
        } catch (Exception e) {
            log.error("Import failed: {}", e.getMessage());
            return ImportResult.failure("Import failed: "+e.getMessage());
        }

    }

//    @Scheduled(cron = "0 0 3 * * *") //Daily 3 am
//    public void scheduledImport(){
//        log.info("Running scheduled PUJOS_JSON import");
//        importPujoJson();
//    }

    public ImportResult manualImport(){
        return importPujoJson();
    }

    private ExternalPandalResponseDTO fetchExternalData() {
        // 1. Try local classpath file first
        try {
            Resource resource = resourceLoader.getResource("classpath:PUJA.json");
            if (resource.exists()) {
                log.info("Loading PUJA.json from classpath");
                return objectMapper.readValue(resource.getInputStream(), ExternalPandalResponseDTO.class);
            }
        } catch (Exception e) {
            log.warn("Local PUJA.json not found or invalid: {}", e.getMessage());
        }

        // 2. Fallback to GitHub URL if configured
        if (pujaJsonUrl != null && !pujaJsonUrl.isBlank()) {
            log.info("Fetching PUJA.json from URL: {}", pujaJsonUrl);
            try {
                return restTemplate.getForEntity(pujaJsonUrl, ExternalPandalResponseDTO.class).getBody();
            } catch (Exception e) {
                log.error("URL fetch failed: {}", e.getMessage());
            }
        }

        log.error("No PUJA.json source available");
        return null;
    }

    private ImportStatus processExternalPandal(ExternalPandalDTO ext) {
        // 1. Check exact match by external_id + source
        Optional<PandalEntity> existingOpt = pandalRepository.findByExternalIdAndSource(ext.getId(), dataSource);

        // 2. Map zone to area
        AreaEntity area = mapZoneToArea(ext.getZone());
        if (area == null) {
            log.warn("No area mapping for zone: {}, skipping: {}", ext.getZone(), ext.getName());
            return ImportStatus.SKIPPED;
        }

        // 3. Check for curated match by name+area (different source)
        List<PandalEntity> curatedMatches = pandalRepository.findByNameContainingIgnoreCaseAndAreaId(
                ext.getName(), area.getId());

        PandalEntity curatedMatch = curatedMatches.stream()
                .filter(p -> !dataSource.equals(p.getSource()))
                .findFirst()
                .orElse(null);

        if (existingOpt.isPresent()) {
            // Update existing external record
            updateExternalFields(existingOpt.get(), ext, area);
            return ImportStatus.UPDATED;
        }

        if (curatedMatch != null) {
            // Enrich curated pandal with external data
            enrichCuratedPandal(curatedMatch, ext);
            return ImportStatus.UPDATED;
        }

        // 4. Create new pandal
        PandalEntity newPandal = createPandalFromExternal(ext, area);
        pandalRepository.save(newPandal);
        return ImportStatus.CREATED;
    }

    private AreaEntity mapZoneToArea(String zone) {
        if(zone == null)
            return null;
        String cleanName = zone.trim();
        String areaName = ZONE_TO_AREA.get(cleanName);
        if (areaName == null) {
            log.warn("Unknown zone: '{}' (cleaned: '{}')", zone, cleanName);
            return null;
        }
        return areaCache.get(areaName);
    }

    private void updateExternalFields(PandalEntity entity, ExternalPandalDTO ext, AreaEntity area) {
        entity.setLatitude(ext.getLat());
        entity.setLongitude(ext.getLon());
        entity.setAddress(ext.getAddress());
        entity.setArea(area);
        // Preserve: description, bestTimeToVisit, imageUrl, pandalMetros
    }

    private void enrichCuratedPandal(PandalEntity curated, ExternalPandalDTO ext) {
        if (curated.getLatitude() == null) curated.setLatitude(ext.getLat());
        if (curated.getLongitude() == null) curated.setLongitude(ext.getLon());
        if (curated.getAddress() == null || curated.getAddress().isEmpty())
            curated.setAddress(ext.getAddress());
        if (curated.getExternalId() == null) curated.setExternalId(ext.getId());
        if (curated.getSource() == null) curated.setSource(dataSource);
    }

    private PandalEntity createPandalFromExternal(ExternalPandalDTO ext, AreaEntity area) {
        return PandalEntity.builder()
                .name(ext.getName())
                .area(area)
                .address(ext.getAddress())
                .latitude(ext.getLat())
                .longitude(ext.getLon())
                .externalId(ext.getId())
                .source(dataSource)
                .description(ext.getName()+" is an official registered member of Forum for Durgotsab (FFD 2025). Renowned for its rich cultural traditions, vibrant community participation, and magnificent Durga Puja celebrations.")
                .bestTimeToVisit("evening")
                .build();
    }

    public enum ImportStatus { CREATED, UPDATED, SKIPPED }

    @Getter
    @AllArgsConstructor
    public static class ImportResult {
        private final boolean success;
        private final int created;
        private final int updated;
        private final int skipped;
        private final int errors;
        private final String message;

        public static ImportResult success(int created, int updated, int skipped, int errors) {
            return new ImportResult(true, created, updated, skipped, errors, "Import completed");
        }

        public static ImportResult failure(String message) {
            return new ImportResult(false, 0, 0, 0, 0, message);
        }
    }

}
