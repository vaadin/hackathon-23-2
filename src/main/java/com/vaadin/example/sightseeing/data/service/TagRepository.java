package com.vaadin.example.sightseeing.data.service;

import com.vaadin.example.sightseeing.data.entity.Tag;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, UUID> {

}