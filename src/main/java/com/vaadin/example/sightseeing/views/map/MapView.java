package com.vaadin.example.sightseeing.views.map;

import com.vaadin.example.sightseeing.data.generator.DataGenerator;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.map.configuration.Coordinate;
import com.vaadin.flow.component.map.configuration.View;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;
import com.vaadin.flow.component.map.configuration.style.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
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
    private MenuBar menuBar;


    public MapView() {
        setSizeFull();
        setPadding(false);
        map.getElement().setAttribute("theme", "borderless");
        View view = map.getView();
        view.setCenter(DataGenerator.CENTER);
        view.setZoom(12);
        addAndExpand(map);

        var centerOnMeButton = new Button(VaadinIcon.DOT_CIRCLE.create());
        centerOnMeButton.addClickListener(event -> centerOnUser());
        centerOnMeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        centerOnMeButton.getStyle().set("background-color", "#e2efdd")
                .set("color", "#73877e")
                .set("position", "absolute")
                .set("top", "10px")
                .set("right", "10px");
        add(centerOnMeButton);

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
            initialCoordinates = false;
            map.getFeatureLayer().addFeature(userMarker);
            // Updates center to the user location, just once.
            centerOnUser();
        }
    }

    private void centerOnUser() {
        var coordinates = userMarker.getCoordinates();
        if (coordinates != null && coordinates.getX() != 0 && coordinates.getY() != 0) {
            map.setCenter(coordinates);
        }
    }
}
