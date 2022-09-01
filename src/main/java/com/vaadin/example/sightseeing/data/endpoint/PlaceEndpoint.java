package com.vaadin.example.sightseeing.data.endpoint;

import com.vaadin.example.sightseeing.data.entity.Place;
import com.vaadin.example.sightseeing.data.service.PlaceService;
import dev.hilla.Endpoint;
import dev.hilla.Nonnull;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Endpoint
@RolesAllowed("ADMIN")
public class PlaceEndpoint {

    private final PlaceService service;

    @Autowired
    public PlaceEndpoint(PlaceService service) {
        this.service = service;
    }

    @Nonnull
    public Page<@Nonnull Place> list(Pageable page) {
        return service.list(page);
    }

    public Optional<Place> get(@Nonnull UUID id) {
        return service.get(id);
    }

    @Nonnull
    public Place update(@Nonnull Place entity) {
        return service.update(entity);
    }

    public void delete(@Nonnull UUID id) {
        service.delete(id);
    }

    public int count() {
        return service.count();
    }

}
