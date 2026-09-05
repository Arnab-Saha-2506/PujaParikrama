package com.proj.PujaParikrama.mappers;

import com.proj.PujaParikrama.dto.AreaResponseDTO;
import com.proj.PujaParikrama.entity.AreaEntity;

public class AreaMapper {
    public static AreaResponseDTO toAreaResponseDTO(AreaEntity entity){
        return AreaResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}
