package com.vaadin.example.sightseeing.views.map;

import java.util.List;
import java.util.UUID;

import javax.annotation.security.PermitAll;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.example.sightseeing.data.entity.Place;
import com.vaadin.example.sightseeing.data.generator.DataGenerator;
import com.vaadin.example.sightseeing.data.service.OverpassService;
import com.vaadin.example.sightseeing.data.service.PlaceService;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.map.configuration.Coordinate;
import com.vaadin.flow.component.map.configuration.Feature;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;
import com.vaadin.flow.component.map.events.MouseEventDetails;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoIcon;

@PageTitle("Map")
@Route(value = "map")
@RouteAlias(value = "")
@PermitAll
public class MapView extends VerticalLayout {
    private static final String[] TAGS_OF_INTEREST = { "website" };
    private final PlaceService placeService;
    private final Map map;
    private final Div detailsDiv;
    private final OverpassService overpassService;
    private MarkerFeature userLocation;

    public MapView(final PlaceService placeService, final OverpassService overpassService) {
        this.placeService = placeService;
        this.overpassService = overpassService;
        this.map = new Map();
        this.detailsDiv = new Div();
        this.detailsDiv.addClassNames("ui-map-details");
        this.setSizeFull();
        this.setPadding(false);
        this.setup();
        this.hideDetails();
        this.add(this.detailsDiv);
    }

    private void setup() {
        this.map.getElement().setAttribute("theme", "borderless");
        this.addAndExpand(this.map);

        this.map.addFeatureClickListener(event -> {
            this.hideDetails();
            final MarkerFeature feature = (MarkerFeature) event.getFeature();
            if (feature == this.userLocation) {
                Notification.show("Thats your location", 3000, Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_PRIMARY);
            } else {
                final UUID id = UUID.fromString(feature.getId());
                this.placeService.get(id).ifPresent(place -> this.showFeature(place, event.getMouseDetails()));
            }
        });

        this.map.addClickEventListener(event -> {
            this.hideDetails();
        });

        this.map.addViewMoveEndEventListener(event -> {
            if (event.getZoom() >= 12) {
                this.hideDetails();
                this.populateMap();
            } else {
                Notification.show("Zoom in futher to search for places of interest in the locality", 5000, Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_PRIMARY);
            }
        });

        this.markUserLocation();
    }

    private void markUserLocation() {
        this.updateMyLocation(DataGenerator.CENTER.getX(), DataGenerator.CENTER.getY());

        this.getElement().executeJs("""
                                        var el = this;
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

    @ClientCallable
    private void updateMyLocation(final double lat, final double lon) {
        if (this.userLocation == null) {
            this.userLocation = new MarkerFeature();
            this.map.getFeatureLayer().addFeature(this.userLocation);
        }
        final Coordinate coordinate = new Coordinate(lon, lat);
        this.userLocation.setCoordinates(coordinate);
        this.map.setCenter(coordinate);
        this.map.setZoom(15);
        this.populateMap();
    }

    private void showFeature(final Place place, final MouseEventDetails mouseEventDetails) {
        final VerticalLayout layout = new VerticalLayout();
        layout.add(new Span(place.getName()));

        final Component[] tagsOfInterest = this.processTags(place);
        if (ObjectUtils.isNotEmpty(tagsOfInterest)) {
            final Span tagHeading = new Span("More Information");
            final VerticalLayout tagLayout = new VerticalLayout();
            tagLayout.setPadding(false);
            tagLayout.setSpacing(false);
            tagLayout.add(tagHeading);
            tagLayout.add(this.processTags(place));
            layout.add(tagLayout);
        }

        final int x = mouseEventDetails.getAbsoluteX();
        final int y = mouseEventDetails.getAbsoluteY();
        this.detailsDiv.getElement().getStyle().set("position", "absolute");
        this.detailsDiv.getElement().getStyle().set("top", y + "px");
        this.detailsDiv.getElement().getStyle().set("left", x + "px");
        this.detailsDiv.removeAll();
        this.detailsDiv.add(layout);
        this.showDetails();
    }

    private Component[] processTags(final Place place) {
        return place.getTags()
                    .stream()
                    .filter(tag -> StringUtils.equalsAny(tag.getName(), TAGS_OF_INTEREST))
                    .map(tag -> switch (tag.getName()) {
                        case "website" -> {
                            final Anchor anchor = new Anchor(tag.getVal(), LumoIcon.ANGLE_RIGHT.create());
                            final Span name = new Span(tag.getName());
                            yield new HorizontalLayout(name, anchor);
                        }
                        default -> new Span();
                    })
                    .toArray(Component[]::new);
    }

    private void hideDetails() {
        this.detailsDiv.setVisible(false);
    }

    private void showDetails() {
        this.detailsDiv.setVisible(true);
    }

    private void populateMap() {
        final Coordinate coordinate = this.map.getView().getCenter();
        List<Place> places = this.placeService.findNearby(coordinate);
        if (places.size() < 10) {
            this.overpassService.storePlaces(coordinate);
            places = this.placeService.findNearby(coordinate);
        }
        final List<String> existingFeatureIds = this.map.getFeatureLayer().getFeatures().stream().map(Feature::getId).toList();
        places.stream()
              .filter(place -> !existingFeatureIds.contains(place.getId().toString()))
              .forEach(place -> {
                  final Coordinate coordinates = new Coordinate(place.getX(), place.getY());
                  final MarkerFeature marker = new MarkerFeature(coordinates);
                  marker.setId(place.getId().toString());
                  this.map.getFeatureLayer().addFeature(marker);
              });

    }

}
