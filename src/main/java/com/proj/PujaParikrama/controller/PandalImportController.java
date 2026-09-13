package com.proj.PujaParikrama.controller;

import com.proj.PujaParikrama.service.PandalImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/pandals/import")
public class PandalImportController {

    private final PandalImportService pandalImportService;

    @PostMapping("/pujos-json")
    public ResponseEntity<PandalImportService.ImportResult> importPujoJson(){
        PandalImportService.ImportResult result = pandalImportService.manualImport();
        return ResponseEntity.ok().body(result);
    }

    public ResponseEntity<String> getImportStatus(){
        return ResponseEntity.ok("Import service ready. Default source: classpath:PUJA.json");
    }
}
