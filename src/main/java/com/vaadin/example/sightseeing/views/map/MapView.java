package com.vaadin.example.sightseeing.views.map;

import com.vaadin.example.sightseeing.data.generator.DataGenerator;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.map.configuration.Coordinate;
import com.vaadin.flow.component.map.configuration.View;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.security.VaadinAwareSecurityContextHolderStrategy;
import org.springframework.security.core.context.SecurityContext;

import javax.annotation.security.PermitAll;

@PageTitle("Map")
@Route(value = "map")
@RouteAlias(value = "")
@PermitAll
public class MapView extends VerticalLayout {

    private Map map = new Map();

    MarkerFeature myLocation = null;

    public MapView() {

        setSizeFull();
        setPadding(false);
        map.getElement().setAttribute("theme", "borderless");
        map.addClickEventListener(mapClickEvent -> {
//            mapClickEvent.getMouseDetails().getButtonName();
            Coordinate coordinate = mapClickEvent.getCoordinate();
            MarkerFeature clickedMarker = new MarkerFeature(coordinate);
            map.getFeatureLayer().addFeature(clickedMarker);
            map.setCenter(coordinate);
        });

        View view = map.getView();
        view.setCenter(DataGenerator.CENTER);
        view.setZoom(12);

        VaadinAwareSecurityContextHolderStrategy securityContextHolderStrategy = new VaadinAwareSecurityContextHolderStrategy();
        SecurityContext securityContext = securityContextHolderStrategy.getContext();

        securityContext.getAuthentication().getAuthorities();


        Anchor anchorTags = new Anchor("/tags", "Tags");
        Anchor anchorPlaces = new Anchor("/places", "Places");


        addAndExpand(map);

        getElement().executeJs("""
                    var el = this;
                    // https://developer.mozilla.org/en-US/docs/Web/API/Geolocation/watchPosition
                    navigator.geolocation.watchPosition(
                      position => {
                        el.$server.updateMyLocation(position.coords.latitude,position.coords.longitude);
                      },
                      error => {
                         // those ever happen for the great developers :-)
                      },
                      {enableHighAccuracy: true, timeout: 5000, maximumAge: 1000 }
                    );
                """);
    }

    /**
     * Called by the browser on new geolocation updates
     *
     * @param lat latitude
     * @param lon longitude
     */
    @ClientCallable
    private void updateMyLocation(double lat, double lon) {
        if (myLocation == null) {
            myLocation = new MarkerFeature();
            map.getFeatureLayer().addFeature(myLocation);
        }
        Coordinate coordinate = new Coordinate(lon, lat);
        myLocation.setCoordinates(coordinate);
        map.setCenter(coordinate);
    }

}
