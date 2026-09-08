package com.proj.PujaParikrama.controller;

import com.proj.PujaParikrama.dto.RouteRequestDTO;
import com.proj.PujaParikrama.dto.RouteResponseDTO;
import com.proj.PujaParikrama.service.RouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/routes")
public class RouteController {

    private final RouteService routeService;

    @PostMapping
    public ResponseEntity<RouteResponseDTO> generateRoute(@Valid @RequestBody RouteRequestDTO requestDTO){
        RouteResponseDTO response = routeService.generateRoute(requestDTO);
        return ResponseEntity.ok().body(response);
    }
}
