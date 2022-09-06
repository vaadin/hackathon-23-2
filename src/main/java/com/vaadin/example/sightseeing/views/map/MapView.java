package com.vaadin.example.sightseeing.views.map;

import com.vaadin.example.sightseeing.data.entity.Place;
import com.vaadin.example.sightseeing.data.generator.DataGenerator;
import com.vaadin.example.sightseeing.data.service.PlaceService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.map.configuration.Coordinate;
import com.vaadin.flow.component.map.configuration.Feature;
import com.vaadin.flow.component.map.configuration.View;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;
import com.vaadin.flow.component.map.configuration.layer.FeatureLayer;
import com.vaadin.flow.component.map.configuration.style.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import java.util.List;
import java.util.UUID;

import javax.annotation.security.PermitAll;

import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Map")
@Route(value = "map")
@RouteAlias(value = "")
@PermitAll
public class MapView extends VerticalLayout {

    private Map map = new Map();
	Dialog dialog = new Dialog();

    @Autowired
    public MapView(PlaceService placeService) {
        setSizeFull();
        setPadding(false);
        map.getElement().setAttribute("theme", "borderless");
        View view = map.getView();
        view.setCenter(DataGenerator.CENTER);
        view.setZoom(12);
        FeatureLayer features = map.getFeatureLayer();
        populateMarkers(placeService, features);
        
        map.addClickEventListener(event -> {
        	Coordinate coordinate = event.getCoordinate();
        	Place place = initPlace(coordinate);
        	TextField nameField = new TextField("Name");
        	FormLayout form = createForm(place, nameField);
        	Button save = new Button("Save");
        	save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        	Button cancel = new Button("Cancel");
        	dialog.removeAll();
        	dialog.setHeaderTitle(place.getId().toString());
        	dialog.getFooter().add(cancel,save);
        	dialog.add(form);
        	dialog.open();
        	save.addClickListener(e -> {
        		place.setName(nameField.getValue());
        		placeService.update(place);
        		MarkerFeature marker = createMarkerForPlace(coordinate, place);
        		features.addFeature(marker);
        		dialog.close();
        	});
        	cancel.addClickListener(e -> {
        		dialog.close();
        	});
        });
        
        map.addFeatureClickListener(event -> {
        	Feature feature = event.getFeature();
        	UI.getCurrent().navigate("places/"+feature.getId()+"/edit");
        	dialog.close();
        });
        addAndExpand(map);
    }

	private void populateMarkers(PlaceService placeService, FeatureLayer features) {
		List<Place> places = placeService.findAll();
        places.forEach(place -> {
        	MarkerFeature feature = new MarkerFeature();
        	Coordinate coord = new Coordinate(place.getX(), place.getY());
        	feature.setId(place.getId().toString());
        	feature.setCoordinates(coord);
        	features.addFeature(feature);
        });
	}

	private MarkerFeature createMarkerForPlace(Coordinate coordinate, Place place) {
		MarkerFeature marker = new MarkerFeature();
		marker.setCoordinates(coordinate);
		marker.setId(place.getId().toString());
		return marker;
	}

	private FormLayout createForm(Place place, TextField nameField) {
		FormLayout form = new FormLayout();
		TextField xField = new TextField("X");
		xField.setValue(place.getX().toString());
		TextField yField = new TextField("Y");
		yField.setValue(place.getY().toString());
		xField.setReadOnly(true);
		yField.setReadOnly(true);
		form.add(nameField,xField,yField);
		return form;
	}

	private Place initPlace(Coordinate coordinate) {
		Place place = new Place();
		place.setX(coordinate.getX());
		place.setY(coordinate.getY());
		place.setId(UUID.randomUUID());
		return place;
	}
}
