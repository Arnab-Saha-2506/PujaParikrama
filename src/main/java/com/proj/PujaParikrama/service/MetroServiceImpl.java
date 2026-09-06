package com.proj.PujaParikrama.service;

import com.proj.PujaParikrama.dto.LineResponseDTO;
import com.proj.PujaParikrama.dto.MetroStationResponseDTO;
import com.proj.PujaParikrama.dto.PandalResponseDTO;
import com.proj.PujaParikrama.entity.MetroStationEntity;
import com.proj.PujaParikrama.entity.PandalMetroEntity;
import com.proj.PujaParikrama.exception.NotFoundException;
import com.proj.PujaParikrama.mappers.MetroMapper;
import com.proj.PujaParikrama.mappers.PandalMapper;
import com.proj.PujaParikrama.repository.MetroStationRepository;
import com.proj.PujaParikrama.repository.PandalMetroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MetroServiceImpl implements MetroService{

    private final PandalMetroRepository pandalMetroRepository;
    private final MetroStationRepository metroStationRepository;

    @Override
    public List<MetroStationResponseDTO> getNearbyMetroStations(Long pandalId) {
        List<PandalMetroEntity> pandalMetros = pandalMetroRepository.findByPandalIdWithMetroStation(pandalId);

        if(pandalMetros.isEmpty()){
            throw new NotFoundException("No metro stations found for pandal id: "+pandalId);
        }

        return pandalMetros.stream()
                .map(MetroMapper::toMetroStationResponseDTO)
                .toList();

//        return List.of();
    }

    @Override
    public List<LineResponseDTO> getAllLines() {
        List<MetroStationEntity> stations = metroStationRepository.findAll();
        Set<String> lines = new HashSet<>();

        for(MetroStationEntity station : stations){
            if(station.getLine() != null && !station.getLine().isEmpty()){
                {
                    String[] parts = station.getLine().split(",");
                    for(String part : parts)
                        lines.add(part.trim());
                }
            }
        }

        return lines.stream()
                .map(LineResponseDTO::new)
                .sorted(Comparator.comparing(LineResponseDTO::getName))
                .toList();
    }

    @Override
    public List<MetroStationResponseDTO> getStationsByLine(String line) {
        List<MetroStationEntity> stations = metroStationRepository.findAll();
        String searchLine = line.trim();

        return stations.stream()
                .filter(station -> {
                    if(station.getLine() == null) return false;
                    String[] parts = station.getLine().split(",");
                    return Arrays.stream(parts)
                            .map(String::trim)
                            .anyMatch(l -> l.equalsIgnoreCase(searchLine));
                })
                .map(MetroMapper::toMetroStationResponseDTO)
                .toList();

//        return List.of();
    }

    @Override
    public List<PandalResponseDTO> getPandalsByMetroStation(Long metroStationId) {
        List<PandalMetroEntity> pandalMetros = pandalMetroRepository.findByMetroStation_Id(metroStationId);

        if(pandalMetros.isEmpty()){
            throw new NotFoundException("No pandals found for metro station id: " + metroStationId);
        }

        return pandalMetros.stream()
                .map(PandalMetroEntity::getPandal)
                .map(PandalMapper::toPandalResponseDTO)
                .toList();

//        return List.of();
    }
}
