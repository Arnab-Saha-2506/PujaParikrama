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

    @Query("SELECT p FROM PandalEntity p WHERE p.latitude IS NOT NULL AND p.longitude IS NOT NULL " +
            "AND p.latitude BETWEEN :minLat AND :maxLat " +
            "AND p.longitude BETWEEN :minLon AND :maxLon")
    List<PandalEntity> findByBoundingBox(
            @Param("minLat") double minLat, @Param("maxLat") double maxLat,
            @Param("minLon") double minLon, @Param("maxLon") double maxLon);
}
