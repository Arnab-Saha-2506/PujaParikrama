package com.proj.PujaParikrama.repository;

import com.proj.PujaParikrama.entity.ContactMeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<ContactMeEntity, Long> {
}
