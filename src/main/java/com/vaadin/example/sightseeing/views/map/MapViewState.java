package com.vaadin.example.sightseeing.views.map;

import org.springframework.stereotype.Component;

import com.vaadin.flow.component.map.configuration.Coordinate;
import com.vaadin.flow.spring.annotation.UIScope;

@Component
@UIScope
public class MapViewState {
  Coordinate center;
  Coordinate position;
  Float zoom;
  Boolean defaultSource = true;
}
