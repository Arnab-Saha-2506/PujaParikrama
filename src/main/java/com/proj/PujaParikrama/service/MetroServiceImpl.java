package com.proj.PujaParikrama.service;

import com.proj.PujaParikrama.dto.MetroStationResponseDTO;
import com.proj.PujaParikrama.entity.PandalMetroEntity;
import com.proj.PujaParikrama.exception.NotFoundException;
import com.proj.PujaParikrama.mappers.MetroMapper;
import com.proj.PujaParikrama.mappers.PandalMapper;
import com.proj.PujaParikrama.repository.PandalMetroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MetroServiceImpl implements MetroService{

    private final PandalMetroRepository pandalMetroRepository;

    @Override
    public List<MetroStationResponseDTO> getNearbyMetroStations(Long pandalId) {
        List<PandalMetroEntity> pandalMetros = pandalMetroRepository.findByPandalId(pandalId);

        if(pandalMetros.isEmpty()){
            throw new NotFoundException("No metro stations found for pandal id: "+pandalId);
        }

        return pandalMetros.stream()
                .map(MetroMapper::toMetroStationResponseDTO)
                .toList();

//        return List.of();
    }
}
