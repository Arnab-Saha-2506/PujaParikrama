package com.proj.PujaParikrama.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class PandalEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    private AreaEntity area;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "best_time_to_visit", length = 100)
    private String bestTimeToVisit;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "pandal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PandalMetroEntity> pandalMetros;
}
