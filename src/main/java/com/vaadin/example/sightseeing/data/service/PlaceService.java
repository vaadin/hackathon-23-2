package com.vaadin.example.sightseeing.data.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.vaadin.example.sightseeing.data.entity.Place;
import com.vaadin.flow.component.map.configuration.Coordinate;

@Service
public class PlaceService {

    private final PlaceRepository repository;

    @Autowired
    public PlaceService(final PlaceRepository repository) {
        this.repository = repository;
    }

    public Optional<Place> get(final UUID id) {
        return this.repository.findById(id);
    }

    public Place update(final Place entity) {
        return this.repository.save(entity);
    }

    public void delete(final UUID id) {
        this.repository.deleteById(id);
    }

    public Page<Place> list(final Pageable pageable) {
        return this.repository.findAll(pageable);
    }

    public int count() {
        return (int) this.repository.count();
    }

    public List<Place> findNearby(final Coordinate coordinate) {
        return this.repository.findNearby(coordinate.getX(), coordinate.getY());
    }

}
