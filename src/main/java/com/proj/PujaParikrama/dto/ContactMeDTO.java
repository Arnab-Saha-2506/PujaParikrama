package com.proj.PujaParikrama.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactMeDTO {
    private String type;
    private String name;
    private String email;
    private String message;
}
