package com.proj.PujaParikrama.service;

import com.proj.PujaParikrama.dto.RouteRequestDTO;
import com.proj.PujaParikrama.dto.RouteResponseDTO;

public interface RouteService {
    RouteResponseDTO generateRoute(RouteRequestDTO requestDTO);
}
