package com.proj.PujaParikrama.dto;

import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AreaResponseDTO {
    private Long id;
    private String name;
}
