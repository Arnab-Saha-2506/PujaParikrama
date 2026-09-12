package com.proj.PujaParikrama.repository;

import com.proj.PujaParikrama.entity.PandalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PandalRepository extends JpaRepository<PandalEntity, Long> {

    @Query("SELECT DISTINCT p FROM PandalEntity p " +
            "JOIN FETCH p.area "+
            "LEFT JOIN FETCH p.pandalMetros pm "+
            "LEFT JOIN FETCH pm.metroStation "+
            "WHERE p.area.id = :areaId")
    List<PandalEntity> findByAreaId(@Param("areaId") Long areaId);
    boolean existsByNameAndAreaId(String name, Long areaId);
}
