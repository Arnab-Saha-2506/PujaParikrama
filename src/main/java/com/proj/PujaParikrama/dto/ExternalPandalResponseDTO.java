package com.proj.PujaParikrama.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExternalPandalResponseDTO {
    private List<ExternalPandalDTO> result;
}
