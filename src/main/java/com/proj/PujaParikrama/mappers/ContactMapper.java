package com.proj.PujaParikrama.mappers;

import com.proj.PujaParikrama.dto.ContactMeDTO;
import com.proj.PujaParikrama.entity.ContactMeEntity;

public class ContactMapper {
    public static ContactMeEntity toEntity(ContactMeDTO dto){
        return ContactMeEntity.builder()
                .type(dto.getType())
                .name(dto.getName())
                .email(dto.getEmail())
                .message(dto.getMessage())
                .build();
    }
}
