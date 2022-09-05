package com.vaadin.example.sightseeing.views.map;

import com.vaadin.example.sightseeing.components.GeoLocation;
import com.vaadin.example.sightseeing.data.entity.Place;
import com.vaadin.example.sightseeing.data.generator.DataGenerator;
import com.vaadin.example.sightseeing.data.service.PlaceService;
import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.map.configuration.Coordinate;
import com.vaadin.flow.component.map.configuration.View;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import javax.annotation.security.PermitAll;

@PageTitle("Map")
@Route(value = "map")
@RouteAlias(value = "")
@PermitAll
public class MapView extends VerticalLayout {

    private MarkerFeature markerFeature;
    private Map map = new Map();

    public MapView(@Autowired PlaceService placeService) {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        map.getElement().setAttribute("theme", "borderless");
        View view = map.getView();
        view.setCenter(DataGenerator.CENTER);
        view.setZoom(12);
        addAndExpand(map);

        final var places = placeService.list(PageRequest.of(0, Integer.MAX_VALUE)).getContent();
        for (Place place : places) {
            final var coordinate = new Coordinate(place.getX(), place.getY());
            final var feature = new MarkerFeature(coordinate);
            map.getFeatureLayer().addFeature(feature);
        }

        var geoLocation = new GeoLocation();
        add(geoLocation);
        geoLocation.addChangeListener(e -> {
            if (markerFeature == null) {
                markerFeature = new MarkerFeature();
                markerFeature.setIcon(MarkerFeature.POINT_ICON);
            }
            markerFeature.setCoordinates(new Coordinate(e.getLongitude(), e.getLatitude()));
            map.getFeatureLayer().addFeature(markerFeature);
        });
    }


}
