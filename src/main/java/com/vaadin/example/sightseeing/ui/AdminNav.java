package com.vaadin.example.sightseeing.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class AdminNav extends HorizontalLayout {

    public AdminNav(String current) {
        addClassName("admin-nav");

        // TODO: more accessible navigation
        Button mapButton = new Button("Map",
                e -> getUI().get().navigate("map"));
        mapButton.setVisible(!"map".equals(current));
        mapButton.setId("mapButton");

        Button placesButton = new Button("Places",
                e -> getUI().get().navigate("places"));
        placesButton.setVisible(!"places".equals(current));
        placesButton.setId("placesButton");

        Button tagsButton = new Button("Tags",
                e -> getUI().get().navigate("tags"));
        tagsButton.setVisible(!"tags".equals(current));
        tagsButton.setId("tagsButton");

        add(mapButton, placesButton, tagsButton);
        expand(mapButton, placesButton, tagsButton);
        setWidthFull();
    }
}
