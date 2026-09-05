package com.proj.PujaParikrama.controller;

import com.proj.PujaParikrama.dto.LineResponseDTO;
import com.proj.PujaParikrama.dto.MetroStationResponseDTO;
import com.proj.PujaParikrama.dto.PandalResponseDTO;
import com.proj.PujaParikrama.service.MetroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MetroController {
    private final MetroService metroService;

    @GetMapping("/pandals/{pandalId}/metros")
    public ResponseEntity<List<MetroStationResponseDTO>> getNearbyMetroStations(@PathVariable Long pandalId){
        List<MetroStationResponseDTO> response = metroService.getNearbyMetroStations(pandalId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/metro/lines")
    public ResponseEntity<List<LineResponseDTO>> getAllLines(){
        List<LineResponseDTO> response = metroService.getAllLines();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/metro/lines/{lineName}/stations")
    public ResponseEntity<List<MetroStationResponseDTO>> getStationsByLine(@PathVariable String lineName){
        List<MetroStationResponseDTO> responseDTOS = metroService.getStationsByLine(lineName);
        return ResponseEntity.ok().body(responseDTOS);
    }

    @GetMapping("/metro/stations/{metroStationId}/pandals")
    public ResponseEntity<List<PandalResponseDTO>> getPandalsByMetroStation(@PathVariable Long metroStationId){
        List<PandalResponseDTO> responseDTOS = metroService.getPandalsByMetroStation(metroStationId);
        return ResponseEntity.ok().body(responseDTOS);
    }
}
