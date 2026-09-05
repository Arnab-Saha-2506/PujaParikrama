package com.proj.PujaParikrama.service;

import com.proj.PujaParikrama.dto.AreaResponseDTO;
import com.proj.PujaParikrama.dto.MetroStationResponseDTO;

import java.util.List;

public interface MetroService {
    List<MetroStationResponseDTO> getNearbyMetroStations(Long pandalId);
}
