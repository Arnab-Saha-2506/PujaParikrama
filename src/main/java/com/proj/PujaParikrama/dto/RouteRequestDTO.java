package com.proj.PujaParikrama.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RouteRequestDTO {

    @NotEmpty(message = "At least one pandal must be selected")
    private List<Long> pandalIds;
}
