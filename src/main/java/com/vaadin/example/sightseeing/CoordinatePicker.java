package com.vaadin.example.sightseeing;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.map.configuration.Coordinate;
import com.vaadin.flow.component.map.configuration.View;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;

public class CoordinatePicker extends CustomField<Coordinate> {

    private Coordinate coordinate;

    private Span text = new Span("Unknown location");

    private final Coordinate defaultCenter;

    public CoordinatePicker(String label, Coordinate defaultCenter) {
        this.defaultCenter = defaultCenter;
        setLabel(label);
        add(text);
        add(new Button("Pick", event -> {
            MarkerFeature marker = new MarkerFeature();

            Map map = new Map();
            map.setSizeFull();
            Dialog mapDialog = new Dialog(map);
            mapDialog.setWidth("80vw");
            mapDialog.setHeight("80vh");
            mapDialog.setModal(true);
            mapDialog.open();

            Button selectButton = new Button("Set", select -> {
                Coordinate coordinates = marker.getCoordinates();
                this.coordinate = coordinates;
                setModelValue(coordinates, select.isFromClient());
                refreshText();
                mapDialog.close();
            });
            selectButton.setEnabled(false);
            selectButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            map.addClickEventListener(click -> {
                marker.setCoordinates(click.getCoordinate());

                if (!selectButton.isEnabled()) {
                    map.getFeatureLayer().addFeature(marker);
                    selectButton.setEnabled(true);
                }
            });

            View view = map.getView();
            view.setZoom(12);

            if (coordinate != null) {
                marker.setCoordinates(coordinate);
                map.getFeatureLayer().addFeature(marker);
                view.setCenter(coordinate);
            } else {
                view.setCenter(defaultCenter);
            }

            mapDialog.getFooter().add(selectButton);
        }));
    }

    @Override
    protected void setPresentationValue(Coordinate newPresentationValue) {
        coordinate = newPresentationValue;
        refreshText();
    }

    private void refreshText() {
        if (coordinate == null) {
            text.setText("Unknown location");
        } else {
            text.setText("@" + coordinate.getX() + " , " + coordinate.getY());
        }
    }

    @Override
    protected Coordinate generateModelValue() {
        return coordinate;
    }

}
