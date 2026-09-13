package com.proj.PujaParikrama.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExternalPandalDTO {
    private String name;
    private Double lat;
    private Double lon;
    private String address;
    private String zone;
    private String id; //external id from source
    private String city;
}
