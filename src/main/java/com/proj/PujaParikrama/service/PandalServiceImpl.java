package com.proj.PujaParikrama.service;

import com.proj.PujaParikrama.dto.MetroStationResponseDTO;
import com.proj.PujaParikrama.dto.PandalDetailResponseDTO;
import com.proj.PujaParikrama.dto.PandalResponseDTO;
import com.proj.PujaParikrama.entity.AreaEntity;
import com.proj.PujaParikrama.entity.PandalEntity;
import com.proj.PujaParikrama.entity.PandalMetroEntity;
import com.proj.PujaParikrama.exception.NotFoundException;
import com.proj.PujaParikrama.mappers.MetroMapper;
import com.proj.PujaParikrama.mappers.PandalMapper;
import com.proj.PujaParikrama.repository.AreaRepository;
import com.proj.PujaParikrama.repository.PandalMetroRepository;
import com.proj.PujaParikrama.repository.PandalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PandalServiceImpl implements PandalService{

    private final PandalRepository pandalRepository;
    private final AreaRepository areaRepository;
    private final PandalMetroRepository pandalMetroRepository;

    @Override
    public List<PandalResponseDTO> getPandalsByArea(Long areaId) {
        AreaEntity area;
        try{
            area = areaRepository.getReferenceById(areaId);
        }
        catch (Exception e){
            throw new NotFoundException("Area not found with id: "+areaId);
        }

        List<PandalEntity> pandalEntityList = pandalRepository.findByAreaId(areaId);

        return pandalEntityList.stream()
                .map(PandalMapper::toPandalResponseDTO)
                .toList();
    }

    @Override
    public PandalDetailResponseDTO getPandalDetails(Long pandalId) {
        PandalEntity pandal;

        try {
            pandal = pandalRepository.getReferenceById(pandalId);
        } catch (Exception e) {
            throw new NotFoundException("Pandal not found with id: "+pandalId);
        }

        AreaEntity area = pandal.getArea();

        List<PandalMetroEntity> pandalMetros = pandalMetroRepository.findByPandalId(pandalId);

        List<MetroStationResponseDTO> nearbyMetros = pandalMetros.stream()
                .map(MetroMapper::toMetroStationResponseDTO)
                .toList();

        return PandalMapper.toPandalDetailResponseDTO(pandal, area, nearbyMetros);

//        return null;
    }
}
