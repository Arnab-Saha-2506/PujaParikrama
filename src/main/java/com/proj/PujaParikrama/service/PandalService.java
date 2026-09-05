package com.proj.PujaParikrama.service;

import com.proj.PujaParikrama.dto.PandalDetailResponseDTO;
import com.proj.PujaParikrama.dto.PandalResponseDTO;

import java.util.List;

public interface PandalService {
    List<PandalResponseDTO> getPandalsByArea(Long areaId);
    PandalDetailResponseDTO getPandalDetails(Long pandalId);
}
