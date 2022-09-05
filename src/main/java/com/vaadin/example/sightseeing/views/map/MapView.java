package com.vaadin.example.sightseeing.views.map;

import com.vaadin.example.sightseeing.data.generator.DataGenerator;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.map.configuration.Coordinate;
import com.vaadin.flow.component.map.configuration.View;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;
import com.vaadin.flow.component.map.configuration.style.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import javax.annotation.security.PermitAll;

import org.vaadin.firitin.geolocation.Geolocation;
import org.vaadin.firitin.geolocation.GeolocationCoordinates;

@PageTitle("Map")
@Route(value = "map")
@RouteAlias(value = "")
@PermitAll
public class MapView extends VerticalLayout {

    private Map map = new Map();
    private MarkerFeature userMarker;
    private boolean initialCoordinates = true;


    public MapView() {
        setSizeFull();
        setPadding(false);
        map.getElement().setAttribute("theme", "borderless");
        View view = map.getView();
        view.setCenter(DataGenerator.CENTER);
        view.setZoom(12);
        addAndExpand(map);

        userMarker = new MarkerFeature();
        var userIconOptions = new Icon.Options();
        //userIconOptions.setImgSize(new Icon.ImageSize(200, 200));
        userIconOptions.setSrc("icons/icon.png");
        var userIcon = new Icon(userIconOptions);
        userIcon.setScale(0.1f);
        userMarker.setIcon(userIcon);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        if (attachEvent.isInitialAttach()) {
            Geolocation.watchPosition((event)-> refreshUserMarker(event.getCoords()), null);
        }
    }

    public void refreshUserMarker(GeolocationCoordinates coords) {
        userMarker.setCoordinates(new Coordinate(coords.getLongitude(), coords.getLatitude()));
        if (initialCoordinates) {
            map.getFeatureLayer().addFeature(userMarker);
            // Updates center to the user location, just once.
            map.setCenter(userMarker.getCoordinates());
        }
    }
}
