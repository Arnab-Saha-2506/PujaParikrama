package com.proj.PujaParikrama.service;

import com.proj.PujaParikrama.dto.AreaResponseDTO;
import com.proj.PujaParikrama.entity.AreaEntity;
import com.proj.PujaParikrama.exception.NotFoundException;
import com.proj.PujaParikrama.mappers.AreaMapper;
import com.proj.PujaParikrama.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AreaServiceImpl implements AreaService{

    private final AreaRepository areaRepository;

    @Override
    public List<AreaResponseDTO> getAllAreas() {
        List<AreaEntity> areaEntityList = areaRepository.findAll();
        return areaEntityList.stream()
                .map(AreaMapper::toAreaResponseDTO)
                .toList();
    }

    @Override
    public AreaResponseDTO getAreaById(Long id) {
        try{
            AreaEntity entity = areaRepository.getReferenceById(id);
            return AreaMapper.toAreaResponseDTO(entity);
        }
        catch (Exception e){
            throw new NotFoundException("Area not found with id: "+id);
        }
    }
}
