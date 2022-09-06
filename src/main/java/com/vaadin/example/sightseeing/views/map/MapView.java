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
import com.vaadin.example.sightseeing.views.places.PlacesView;
import com.vaadin.example.sightseeing.views.tags.TagsView;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
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
    private HashMap<Feature, Place> places = new HashMap<>();
    private Div text = new Div(new Text(""));
    private Div body = new Div();
    private Notification notification = createNotification(text, body);
    private MarkerFeature current;
    private String filter = null;
    private PlaceRepository repo;


    @Autowired
    public MapView(PlaceRepository repo) {
        this.repo = repo;

        setSizeFull();
        setPadding(false);

        map.addThemeVariants(MapVariant.BORDERLESS);

        View view = map.getView();
        view.setCenter(DataGenerator.CENTER);
        view.setZoom(14);
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
                + "   elm.$server.newPlace(coord[0], coord[1]);"
                + " } else if (feats.length == 1) {"
                + "   elm.$server.editPlace(feats[0].id);"
                + " }"
                + "});"
                + "", map.getElement());
        map.addFeatureClickListener(e -> showPlace(places.get(e.getFeature())));
        addAndExpand(map, buttons);
        refreshPOIs();
    }

    @ClientCallable
    private void updateLocation(double lon, double lat) {
        Coordinate coordinate = new Coordinate(lon, lat);
        if (current == null) {
            current = new MarkerFeature(DataGenerator.CENTER,
                    MarkerFeature.PIN_ICON);
            map.getFeatureLayer().addFeature(current);
        }
        current.setCoordinates(coordinate);
    }

    @ClientCallable
    private void newPlace(double lon, double lat) {
        Coordinate coordinate = new Coordinate(lon, lat);
        MarkerFeature feat = new MarkerFeature(coordinate, MarkerFeature.POINT_ICON);
        map.getFeatureLayer().addFeature(feat);
        UI.getCurrent().navigate("places/" + lon + "/" + lat + "/new");
    }

    @ClientCallable
    private void editPlace(String id) {
        System.err.println(id);
        UI.getCurrent().navigate("places/" + id + "/edit");
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
        ((filter == null || filter.isBlank()) ? repo.findAll()
                : repo.findByNameContainingIgnoreCase(filter))
                .stream().filter(p -> p.isEnabled()).forEach(place -> {
                    MarkerFeature feat = new MarkerFeature(new Coordinate(place.getX(), place.getY()),
                            MarkerFeature.POINT_ICON);
                    feat.setId(place.getId().toString());
                    map.getFeatureLayer().addFeature(feat);
                    places.put(feat, place);
                });
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
        String coord = String.format("@%f,%f", p.getY(), p.getX());
        String mapUrl = String.format("https://www.google.com/maps/%s,100m/data=!3m1!1e3", coord);
        return "<ul>"
                + p.getTags().stream().filter(t -> !t.getName().contains(":"))
                        .map(t -> toHtml(t)).collect(Collectors.joining())
                + String.format("<li><b>coordinates</b>: <a target=_blank href=\"%s\">%s</a></li></ul>", mapUrl, coord);
    }

    private String toHtml(Tag t) {
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

    private Component setupButtons() {


        MenuBar buttons = new MenuBar();
        buttons.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
        buttons.addThemeVariants(MenuBarVariant.LUMO_END_ALIGNED);
        buttons.getElement().getStyle().set("position", "absolute");
        buttons.getElement().getStyle().set("right", "0px");

        buttons.addItem(createFilterTextbox());

        MenuItem mapView = createIconItem(buttons, VaadinIcon.ROAD, "Map View");
        MenuItem satView = createIconItem(buttons, VaadinIcon.ROCKET, "Satellite View");
        mapView.setVisible(false);

        TileLayer mapLayer = new TileLayer() {{setSource(new OSMSource());}};
        TileLayer satLayer = new TileLayer() {{setSource(new OSMSource() {{setUrl(SAT_URL);}});}};

        mapView.addClickListener(e -> {
            map.setBackgroundLayer(mapLayer);
            mapView.setVisible(false);
            satView.setVisible(true);
        });
        satView.addClickListener(e -> {
            map.setBackgroundLayer(satLayer);
            mapView.setVisible(true);
            satView.setVisible(false);
        });

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            MenuItem admin = createIconItem(buttons, VaadinIcon.COG_O, "Admin");
            SubMenu adminSubMenu = admin.getSubMenu();
            MenuItem places = adminSubMenu.addItem("Places");
            MenuItem tags = adminSubMenu.addItem("Tags");
            places.addClickListener(e -> UI.getCurrent().navigate(PlacesView.class));
            tags.addClickListener(e -> UI.getCurrent().navigate(TagsView.class));
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
