package com.proj.PujaParikrama.repository;

import com.proj.PujaParikrama.entity.PandalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PandalRepository extends JpaRepository<PandalEntity, Long> {
    List<PandalEntity> findByAreaId(Long areaId);
    boolean existsByNameAndAreaId(String name, Long areaId);
}
