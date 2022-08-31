package com.vaadin.example.sightseeing.data.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.example.sightseeing.data.entity.Place;

import de.westnordost.osmapi.OsmConnection;
import de.westnordost.osmapi.map.data.BoundingBox;
import de.westnordost.osmapi.map.data.Element;
import de.westnordost.osmapi.map.data.LatLon;
import de.westnordost.osmapi.map.data.Node;
import de.westnordost.osmapi.map.data.Relation;
import de.westnordost.osmapi.map.data.Way;
import de.westnordost.osmapi.overpass.MapDataWithGeometryHandler;
import de.westnordost.osmapi.overpass.OverpassMapDataApi;

public class OverpassService {

    private OsmConnection connection = new OsmConnection("https://overpass-api.de/api/", null);
    private OverpassMapDataApi overpass = new OverpassMapDataApi(connection);
    private final String QUERY = "[bbox:%f,%f,%f,%f];(node[tourism=attraction];node[tourism=museum];node[historic];way[amenity=place_of_worship];way[historic][historic!=tomb];relation[amenity=place_of_worship];relation[historic];relation[turism][turism!=hotel];); out body geom;";

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public Set<Place> getPlaces(double minY, double minX, double maxY,
            double maxX) {

        Set<Place> places = new HashSet<>();

        String q = String.format(QUERY, minY, minX, maxY, maxX);
        log.info("QUERING data base\n" + q);

        overpass.killMyQueries();

        overpass.queryElementsWithGeometry(q, new MapDataWithGeometryHandler() {
            void add(Element e, BoundingBox bb) {
                double lat = (bb.getMaxLatitude() - bb.getMinLatitude()) / 2 + bb.getMinLatitude();
                double lng = (bb.getMaxLongitude() - bb.getMinLongitude()) / 2 + bb.getMinLongitude();
                add(e, lat, lng);
            }

            void add(Element e, double lat, double lng) {
                places.add(new Place(e.getId(), lng, lat, e.getTags(), e.getEditedAt()));
            }

            @Override
            public void handle(Relation r, BoundingBox bb,
                    Map<Long, LatLon> nodeGeometries,
                    Map<Long, List<LatLon>> wayGeometries) {
                add(r, bb);
            }

            @Override
            public void handle(Way w, BoundingBox bb, List<LatLon> geometry) {
                add(w, bb);
            }

            @Override
            public void handle(Node n) {
                add(n, n.getPosition().getLatitude(), n.getPosition().getLongitude());
            }

            @Override
            public void handle(BoundingBox w) {
            }
        });

        log.info("QUERY returned " + places.size() + " places.");
        return places;
    }
}
