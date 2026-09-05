package com.proj.PujaParikrama.controller;

import com.proj.PujaParikrama.dto.AreaResponseDTO;
import com.proj.PujaParikrama.service.AreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/areas")
public class AreaController {
    private final AreaService areaService;

    @GetMapping
    public ResponseEntity<List<AreaResponseDTO>> getAllAreas(){
        List<AreaResponseDTO> response = areaService.getAllAreas();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{areaId}")
    public ResponseEntity<AreaResponseDTO> getAreaById(@PathVariable Long areaId){
        AreaResponseDTO responseDTO = areaService.getAreaById(areaId);
        return ResponseEntity.ok().body(responseDTO);

    }
}
