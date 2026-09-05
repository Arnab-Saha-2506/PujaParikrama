package com.proj.PujaParikrama.repository;

import com.proj.PujaParikrama.entity.PandalMetroEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PandalMetroRepository extends JpaRepository<PandalMetroEntity, Long> {
    List<PandalMetroEntity> findByPandalId(Long pandalId);
    boolean existsByPandalIdAndMetroStation_Id(Long pandalId, Long metroId);

    List<PandalMetroEntity> findByMetroStation_Id(Long metroStationId);
}
