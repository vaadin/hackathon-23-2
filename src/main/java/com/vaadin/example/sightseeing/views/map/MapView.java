package com.vaadin.example.sightseeing.views.map;

import java.util.HashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.example.sightseeing.data.entity.Place;
import com.vaadin.example.sightseeing.data.entity.Tag;
import com.vaadin.example.sightseeing.data.generator.DataGenerator;
import com.vaadin.example.sightseeing.data.service.PlaceRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.map.configuration.Coordinate;
import com.vaadin.flow.component.map.configuration.Feature;
import com.vaadin.flow.component.map.configuration.View;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import javax.annotation.security.PermitAll;

@PageTitle("Map")
@Route(value = "map")
@RouteAlias(value = "")
@PermitAll
public class MapView extends VerticalLayout {

    private Map map = new Map();
    private HashMap<Feature, Place> places = new HashMap<>();
    private Div text = new Div(new Text(""));
    private Div body = new Div();
    private Notification notification = createNotification(text, body);

    @Autowired
    public MapView(PlaceRepository repo) {
        setSizeFull();
        setPadding(false);

        map.getElement().setAttribute("theme", "borderless");
        View view = map.getView();
        view.setCenter(DataGenerator.CENTER);
        view.setZoom(14);


        MarkerFeature current = new MarkerFeature(DataGenerator.CENTER,
                MarkerFeature.PIN_ICON);
        map.getFeatureLayer().addFeature(current);

        repo.findAll().forEach(place -> {
            MarkerFeature feat = new MarkerFeature(new Coordinate(place.getX(), place.getY()),
                    MarkerFeature.POINT_ICON);
            map.getFeatureLayer().addFeature(feat);
            places.put(feat, place);
        });

        map.addFeatureClickListener(e -> showPlace(places.get(e.getFeature())));
        addAndExpand(map);
    }

    private Notification createNotification(final Component text, final Component body) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.addClickListener(event -> notification.close());
        text.getElement().getStyle().set("fontSize", "1.5em");
        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        notification.add(layout, body);
        return notification;
    }

    private void showPlace(Place p) {
        if (p != null) {
            text.setText(p.getName());
            body.removeAll();
            body.add(new Html(toHtml(p)));
            notification.open();
        }
    }

    private String toHtml(Place p) {
        return "<ul>"
                + p.getTags().stream().filter(t -> !t.getName().contains(":"))
                        .map(t -> toHtml(t)).collect(Collectors.joining())
                + "<li><b>coordinates</b>: @" + p.getX() + "," + p.getY() + "</li></ul>";
    }

    private String toHtml(Tag t) {
        String name = t.getName().replace("_", "");
        String val = t.getVal();
        if ("wikipedia".equals(t.getName())) {
            val = val.replaceFirst("^(..?):(.+)$", "<a target=_blank href=\"https://$1.wikipedia.org/wiki/$2\">$2</a>");
        } else if ("website".equals(t.getName())) {
            val = "<a target=_blank href=\"" + val + "\">\uD83D\uDD17</a>";
        } else {
            val = val.replace("_", "");
        }
        return "<li><b>" + name + "</b>: " + val + "</li>";
    }
}
