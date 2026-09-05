package com.proj.PujaParikrama.repository;

import com.proj.PujaParikrama.entity.MetroStationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MetroStationRepository extends JpaRepository<MetroStationEntity, Long> {
    Optional<MetroStationEntity> findByName(String name);
    boolean existsByName(String name);
}
