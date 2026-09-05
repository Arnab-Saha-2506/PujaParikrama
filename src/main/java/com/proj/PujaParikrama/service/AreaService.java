package com.proj.PujaParikrama.service;

import com.proj.PujaParikrama.dto.AreaResponseDTO;

import java.util.List;

public interface AreaService {
    List<AreaResponseDTO> getAllAreas();
    AreaResponseDTO getAreaById(Long id);
}
