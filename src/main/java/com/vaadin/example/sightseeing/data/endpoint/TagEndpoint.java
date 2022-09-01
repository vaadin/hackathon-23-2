package com.vaadin.example.sightseeing.data.endpoint;

import com.vaadin.example.sightseeing.data.entity.Tag;
import com.vaadin.example.sightseeing.data.service.TagService;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import dev.hilla.Endpoint;
import dev.hilla.Nonnull;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Endpoint
@AnonymousAllowed
public class TagEndpoint {

    private final TagService service;

    @Autowired
    public TagEndpoint(TagService service) {
        this.service = service;
    }

    @Nonnull
    public Page<@Nonnull Tag> list(Pageable page) {
        return service.list(page);
    }

    public Optional<Tag> get(@Nonnull UUID id) {
        return service.get(id);
    }

    @Nonnull
    public Tag update(@Nonnull Tag entity) {
        return service.update(entity);
    }

    public void delete(@Nonnull UUID id) {
        service.delete(id);
    }

    public int count() {
        return service.count();
    }

}
