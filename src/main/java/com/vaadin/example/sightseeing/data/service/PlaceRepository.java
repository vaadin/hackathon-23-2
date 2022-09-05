package com.vaadin.example.sightseeing.data.service;

import com.vaadin.example.sightseeing.data.entity.Place;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, UUID> {

  Page<Place> findByNameContainingIgnoreCase(String filter, Pageable pageable);
}