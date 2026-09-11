package com.proj.PujaParikrama.service;

import com.proj.PujaParikrama.dto.ContactMeDTO;
import com.proj.PujaParikrama.entity.ContactMeEntity;
import com.proj.PujaParikrama.mappers.ContactMapper;
import com.proj.PujaParikrama.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService{

    private final ContactRepository contactRepository;

    @Override
    public void saveMessage(ContactMeDTO dto) {
        if("Query".equalsIgnoreCase(dto.getType()) && (dto.getEmail() == null || dto.getEmail().trim().isEmpty())){
            throw new IllegalArgumentException("Email is required for queries");
        }

        ContactMeEntity entity = ContactMapper.toEntity(dto);

        contactRepository.save(entity);
    }
}
