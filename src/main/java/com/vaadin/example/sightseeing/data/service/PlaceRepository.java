package com.vaadin.example.sightseeing.data.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.vaadin.example.sightseeing.data.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, UUID> {
    
    @Query("select p from Place p where abs(p.x-:x)<0.1 and abs(p.y-:y)<0.1")
    List<Place> findNearby(double x, double y);
    
}