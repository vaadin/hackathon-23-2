package com.vaadin.example.sightseeing.views.map;

import java.util.HashMap;
import java.util.stream.Collectors;

import javax.annotation.security.PermitAll;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.example.sightseeing.data.entity.Place;
import com.vaadin.example.sightseeing.data.entity.Tag;
import com.vaadin.example.sightseeing.data.generator.DataGenerator;
import com.vaadin.example.sightseeing.data.service.PlaceRepository;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.map.MapVariant;
import com.vaadin.flow.component.map.configuration.Coordinate;
import com.vaadin.flow.component.map.configuration.Feature;
import com.vaadin.flow.component.map.configuration.View;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;
import com.vaadin.flow.component.map.configuration.layer.TileLayer;
import com.vaadin.flow.component.map.configuration.source.OSMSource;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@PageTitle("Map")
@Route(value = "map")
@RouteAlias(value = "")
@PermitAll
@SuppressWarnings("serial")
public class MapView extends VerticalLayout {

    private static final String SAT_URL = "https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}";

    private Map map = new Map();
    Dialog dialog;
    TextField nameField = new TextField("Name");
    private HashMap<Feature, Place> places = new HashMap<>();
    private Div text = new Div(new Text(""));
    private Div body = new Div();
    private Notification notification = createNotification(text, body);
    private MarkerFeature current;
    private String filter = null;
    private PlaceRepository repo;
    private MapViewState state;

    @Autowired
    public MapView(PlaceRepository repo, MapViewState state) {
        this.repo = repo;
        this.state = state;

        setSizeFull();
        setPadding(false);

        map.addThemeVariants(MapVariant.BORDERLESS);

        View view = map.getView();
        view.setCenter(state.center != null ? state.center : DataGenerator.CENTER);
        view.setZoom(state.zoom != null ? state.zoom : 14);
        updateUserPosition();
        Component buttons = setupButtons();

        try {
            MarkerFeature.POINT_ICON.setScale(.17f);
        } catch (@SuppressWarnings("unused") Exception e2) {
            // https://github.com/vaadin/flow-components/issues/3641
        }

        getElement().executeJs(""
                + "const elm = this;"
                + "navigator.geolocation.watchPosition("
                + "  pos => elm.$server.updateLocation(pos.coords.longitude, pos.coords.latitude),"
                + "  err => {}, "
                + "  { enableHighAccuracy: true, timeout: 5000, maximumAge: 1000 }"
                + ");"
                + "const map = $0.configuration;"
                + "map.getViewport().addEventListener('contextmenu', ev => {"
                + " ev.preventDefault();"
                + " const feats = map.getFeaturesAtPixel(map.getEventPixel(ev));"
                + " if (feats.length == 0) {"
                + "   const coord = map.getCoordinateFromPixel([ev.layerX,ev.layerY]);"
                + "  console.log('asfasaf', coord);"
                + "   elm.$server.newPlace(coord[0], coord[1]);"
                + " } else if (feats.length == 1) {"
                + "   elm.$server.editPlace(feats[0].id);"
                + " }"
                + "});"
                + "", map.getElement());
        map.addFeatureClickListener(e -> showPlaceInfo(places.get(e.getFeature())));
        map.addViewMoveEndEventListener(e -> {
            state.center = map.getCenter();
            state.zoom = map.getZoom();
            state.position = current.getCoordinates();
        });
        addAndExpand(map, buttons);
        refreshPOIs();
    }

    private void updateUserPosition() {
        if (current == null) {
            current = new MarkerFeature(state.position != null ? state.position : DataGenerator.CENTER,
                    MarkerFeature.PIN_ICON);
            map.getFeatureLayer().addFeature(current);
        }
    }

    @ClientCallable
    private void updateLocation(double lon, double lat) {
        Coordinate position = new Coordinate(lon, lat);
        state.position = position;
        updateUserPosition();
        current.setCoordinates(position);
    }

    @ClientCallable
    private void newPlace(double lon, double lat) {
        Coordinate coordinate = new Coordinate(lon, lat);
        newPlace(coordinate);
        // UI.getCurrent().navigate("places/" + lon + "/" + lat + "/new");
    }

    private void newPlace(Coordinate coordinate) {
        Place place = new Place(null, coordinate.getX(), coordinate.getY(), null, null);

        if (dialog == null) {
            dialog = new Dialog();
            Button save = new Button("Save");
            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            Button cancel = new Button("Cancel");
            dialog.setHeaderTitle("New Place");
            dialog.getFooter().add(cancel, save);
            save.addClickListener(e -> {
                place.setName(nameField.getValue());
                Place saved = repo.save(place);
                addPointToMap(saved);
                dialog.close();
            });
            cancel.addClickListener(e -> {
                dialog.close();
            });
        }
        FormLayout form = createForm(place);
        dialog.removeAll();
        dialog.add(form);
        dialog.open();
    }

    private FormLayout createForm(Place place) {
        nameField.clear();
        FormLayout form = new FormLayout();
        TextField xField = new TextField("X");
        xField.setValue(place.getX().toString());
        TextField yField = new TextField("Y");
        yField.setValue(place.getY().toString());
        xField.setReadOnly(true);
        yField.setReadOnly(true);
        form.add(nameField, xField, yField);
        return form;
    }

    @ClientCallable
    private void editPlace(String id) {
        if (current.getId().equals(id)) {
            newPlace(current.getCoordinates().getX(), current.getCoordinates().getY());
        } else {
            UI.getCurrent().navigate("places/" + id + "/edit");
        }
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

    private Component createFilterTextbox() {
        TextField textField = new TextField();
        textField.getElement().getStyle().set("background", "var(--lumo-tint-60pct)");
        textField.getElement().getStyle().set("padding", "4px");
        textField.getElement().getStyle().set("borderRadius", "5px");

        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.setClearButtonVisible(true);
        textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        textField.setPlaceholder("Search");
        textField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        textField.addValueChangeListener(e -> {
            this.filter = e.getValue();
            refreshPOIs();
        });
        return textField;
    }

    private void refreshPOIs() {
        places.keySet().forEach(f -> map.getFeatureLayer().removeFeature(f));
        places.clear();
        ((filter == null || filter.isBlank()) ? repo.findAllByEnabledTrue()
                : repo.findByNameContainingIgnoreCaseAndEnabledTrue(filter))
                .forEach(place -> addPointToMap(place));
    }

    private void addPointToMap(Place place) {
        MarkerFeature feat = new MarkerFeature(new Coordinate(place.getX(), place.getY()),
                MarkerFeature.POINT_ICON);
        feat.setId(place.getId().toString());
        map.getFeatureLayer().addFeature(feat);
        places.put(feat, place);
    }

    private void showPlaceInfo(Place p) {
        if (p != null) {
            text.setText(p.getName());
            body.removeAll();
            body.add(new Html(placeToHtml(p)));
            notification.open();
        }
    }

    private String placeToHtml(Place p) {
        String coord = String.format("@%f,%f", p.getY(), p.getX());
        String mapUrl = String.format("https://www.google.com/maps/%s,100m/data=!3m1!1e3", coord);
        return "<ul>"
                + p.getTags().stream().filter(t -> t.isEnabled() && !t.getName().contains(":"))
                        .map(t -> tagToHtml(t)).collect(Collectors.joining())
                + String.format("<li><b>coordinates</b>: <a target=_blank href=\"%s\">%s</a></li></ul>", mapUrl, coord);
    }

    private String tagToHtml(Tag t) {
        String name = t.getName().replace("_", " ");
        String val = t.getVal();
        if ("wikipedia".equals(t.getName())) {
            val = val.replaceFirst("^(..?):(.+)$", "<a target=_blank href=\"https://$1.wikipedia.org/wiki/$2\">$2</a>");
        } else if (t.getName().matches("website|url")) {
            val = "<a target=_blank href=\"" + val + "\">\uD83D\uDD17</a>";
        } else {
            val = val.replace("_", " ");
        }
        return "<li><b>" + name + "</b>: " + val + "</li>";
    }

    private void showBackground(TileLayer mapLayer, TileLayer satLayer, Component mapButton, Component satButton) {
        map.setBackgroundLayer(state.defaultSource ? mapLayer : satLayer);
        mapButton.setVisible(!state.defaultSource);
        satButton.setVisible(state.defaultSource);
    }

    private Component setupButtons() {
        MenuBar buttons = new MenuBar();
        buttons.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
        buttons.addThemeVariants(MenuBarVariant.LUMO_END_ALIGNED);
        buttons.getElement().getStyle().set("position", "absolute");
        buttons.getElement().getStyle().set("right", "0px");

        buttons.addItem(createFilterTextbox());

        MenuItem mapButton = createIconItem(buttons, VaadinIcon.ROAD, "Map View");
        MenuItem satButton = createIconItem(buttons, VaadinIcon.ROCKET, "Satellite View");
        TileLayer mapLayer = new TileLayer() {{setSource(new OSMSource());}};
        TileLayer satLayer = new TileLayer() {{setSource(new OSMSource() {{setUrl(SAT_URL);}});}};
        showBackground(mapLayer, satLayer, mapButton, satButton);

        mapButton.addClickListener(e -> {
            state.defaultSource = true;
            showBackground(mapLayer, satLayer, mapButton, satButton);
        });
        satButton.addClickListener(e -> {
            state.defaultSource = false;
            showBackground(mapLayer, satLayer, mapButton, satButton);
        });

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            MenuItem admin = createIconItem(buttons, VaadinIcon.COG_O, "Admin");
            SubMenu adminSubMenu = admin.getSubMenu();
            MenuItem places = adminSubMenu.addItem("Places");
            MenuItem tags = adminSubMenu.addItem("Tags");
            places.addClickListener(e -> UI.getCurrent().navigate("places"));
            tags.addClickListener(e -> UI.getCurrent().navigate("tags"));
        }
        return buttons;
    }

    private MenuItem createIconItem(MenuBar menu, VaadinIcon iconName, String ariaLabel) {
        Icon icon = new Icon(iconName);
        MenuItem item = menu.addItem(icon);
        item.getElement().setAttribute("aria-label", ariaLabel);
        item.getElement().getStyle().set("background", "var(--lumo-tint-60pct)");
        item.getElement().getStyle().set("borderRadius", "5px");
        item.getElement().getStyle().set("width", "36px");
        return item;
    }

}
