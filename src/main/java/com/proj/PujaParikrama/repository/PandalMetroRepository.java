package com.proj.PujaParikrama.repository;

import com.proj.PujaParikrama.entity.PandalMetroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PandalMetroRepository extends JpaRepository<PandalMetroEntity, Long> {


    List<PandalMetroEntity> findByPandalId(Long pandalId);
    boolean existsByPandalIdAndMetroStation_Id(Long pandalId, Long metroId);

    @Query("SELECT pm FROM PandalMetroEntity pm JOIN FETCH pm.metroStation WHERE pm.pandal.id = :pandalId")
    List<PandalMetroEntity> findByPandalIdWithMetroStation(@Param("pandalId") Long pandalId);

    List<PandalMetroEntity> findByMetroStation_Id(Long metroStationId);
}
