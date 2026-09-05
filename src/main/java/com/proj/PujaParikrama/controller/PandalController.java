package com.proj.PujaParikrama.controller;

import com.proj.PujaParikrama.dto.PandalDetailResponseDTO;
import com.proj.PujaParikrama.dto.PandalResponseDTO;
import com.proj.PujaParikrama.service.PandalService;
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
public class PandalController {
    private final PandalService pandalService;

    @GetMapping("/areas/{areaId}/pandals")
    public ResponseEntity<List<PandalResponseDTO>> getPandalsByArea(@PathVariable Long areaId) {
        List<PandalResponseDTO> pandals = pandalService.getPandalsByArea(areaId);
        return ResponseEntity.ok().body(pandals);
    }

    @GetMapping("/pandals/{pandalId}")
    public ResponseEntity<PandalDetailResponseDTO> getPandalDetails(@PathVariable Long pandalId){
        PandalDetailResponseDTO responseDTO = pandalService.getPandalDetails(pandalId);
        return ResponseEntity.ok().body(responseDTO);
    }
}
