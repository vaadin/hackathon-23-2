package com.vaadin.example.sightseeing.endpoints.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaadin.example.sightseeing.data.generator.DataGenerator;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import dev.hilla.Endpoint;
import dev.hilla.Nonnull;

@Endpoint
@AnonymousAllowed
public class MapEndpoint {

    @Nonnull
    public List<@Nonnull Double> getCenter() {
        return new ArrayList<>(Arrays.asList(DataGenerator.CENTER.x, DataGenerator.CENTER.y));
    }
}