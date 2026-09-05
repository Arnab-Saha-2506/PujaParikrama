package com.proj.PujaParikrama.service;

import com.proj.PujaParikrama.dto.AreaResponseDTO;
import com.proj.PujaParikrama.dto.LineResponseDTO;
import com.proj.PujaParikrama.dto.MetroStationResponseDTO;
import com.proj.PujaParikrama.dto.PandalResponseDTO;

import java.util.List;

public interface MetroService {
    List<MetroStationResponseDTO> getNearbyMetroStations(Long pandalId);
    List<LineResponseDTO> getAllLines();
    List<MetroStationResponseDTO> getStationsByLine(String line);
    List<PandalResponseDTO> getPandalsByMetroStation(Long metroStationId);
}
