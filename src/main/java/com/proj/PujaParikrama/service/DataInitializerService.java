package com.proj.PujaParikrama.service;

import com.proj.PujaParikrama.entity.AreaEntity;
import com.proj.PujaParikrama.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializerService implements CommandLineRunner {

    private final AreaRepository areaRepository;

    private static final String[] REQUIRED_AREAS = {"South Kolkata", "North Kolkata", "Central Kolkata", "East Kolkata", "Howrah", "Hoogly"};

    @Override
    public void run(String... args){
        for(String areaName : REQUIRED_AREAS){
            if(!areaRepository.existsByName(areaName)){
                AreaEntity area = AreaEntity.builder().name(areaName).build();
                areaRepository.save(area);
                log.info("Created area: {}", areaName);
            }
        }
    }
}
