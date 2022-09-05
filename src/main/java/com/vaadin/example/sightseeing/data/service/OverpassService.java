package com.vaadin.example.sightseeing.data.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vaadin.example.sightseeing.data.entity.Place;
import com.vaadin.flow.component.map.configuration.Coordinate;

import de.westnordost.osmapi.OsmConnection;
import de.westnordost.osmapi.map.data.BoundingBox;
import de.westnordost.osmapi.map.data.Element;
import de.westnordost.osmapi.map.data.LatLon;
import de.westnordost.osmapi.map.data.Node;
import de.westnordost.osmapi.map.data.Relation;
import de.westnordost.osmapi.map.data.Way;
import de.westnordost.osmapi.overpass.MapDataWithGeometryHandler;
import de.westnordost.osmapi.overpass.OverpassMapDataApi;

@Service
public class OverpassService {
    private static final double RATIO = 0.8;

    private final OsmConnection connection = new OsmConnection("https://overpass-api.de/api/", null);
    private final OverpassMapDataApi overpass = new OverpassMapDataApi(this.connection);
    private final String QUERY = "[bbox:%f,%f,%f,%f];(node[tourism=attraction];node[tourism=museum];node[historic];way[amenity=place_of_worship];way[historic][historic!=tomb];relation[amenity=place_of_worship];relation[historic];relation[turism][turism!=hotel];);     out body geom;";
    // private final String QUERY =
    // "[bbox:%f,%f,%f,%f];(node[tourism=attraction];node[tourism=museum];); out
    // body geom;";
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final PlaceRepository repository;

    @Autowired
    public OverpassService(final PlaceRepository repository) {
        this.repository = repository;
    }

    public void storePlaces(final Coordinate fromCoordinate) {
        this.storePlaces(fromCoordinate, RATIO);
    }

    public void storePlaces(final Coordinate fromCoordinate, final double ratio) {
        final Set<Place> places = this.getPlaces(fromCoordinate.getY() - ratio, fromCoordinate.getX() - ratio,
                                                 fromCoordinate.getY() + ratio, fromCoordinate.getX() + ratio)
                                      .stream()
                                      .filter(p -> p.getName() != null && !p.getName().isBlank()
                                              && !"yes".equals(p.getName()))
                                      .collect(Collectors.toSet());
        this.repository.saveAll(places);
    }

    public Set<Place> getPlaces(final double minY, final double minX, final double maxY,
            final double maxX) {

        final Set<Place> places = new HashSet<>();

        final String q = String.format(this.QUERY, minY, minX, maxY, maxX);
        this.log.info("QUERING data base\n" + q);

        this.overpass.killMyQueries();

        this.overpass.queryElementsWithGeometry(q, new MapDataWithGeometryHandler() {
            void add(final Element e, final BoundingBox bb) {
                final double lat = (bb.getMaxLatitude() - bb.getMinLatitude()) / 2 + bb.getMinLatitude();
                final double lng = (bb.getMaxLongitude() - bb.getMinLongitude()) / 2 + bb.getMinLongitude();
                this.add(e, lat, lng);
            }

            void add(final Element e, final double lat, final double lng) {
                places.add(new Place(e.getId(), lng, lat, e.getTags(), e.getEditedAt()));
            }

            @Override
            public void handle(final Relation r, final BoundingBox bb,
                    final Map<Long, LatLon> nodeGeometries,
                    final Map<Long, List<LatLon>> wayGeometries) {
                this.add(r, bb);
            }

            @Override
            public void handle(final Way w, final BoundingBox bb, final List<LatLon> geometry) {
                this.add(w, bb);
            }

            @Override
            public void handle(final Node n) {
                this.add(n, n.getPosition().getLatitude(), n.getPosition().getLongitude());
            }

            @Override
            public void handle(final BoundingBox w) {
            }
        });

        this.log.info("QUERY returned " + places.size() + " places.");
        return places;
    }
}
