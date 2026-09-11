package com.proj.PujaParikrama.controller;

import com.proj.PujaParikrama.dto.ContactMeDTO;
import com.proj.PujaParikrama.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/message/me")
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<Void> contactMe(@RequestBody ContactMeDTO dto){
        contactService.saveMessage(dto);
        return ResponseEntity.ok().build();
    }
}
