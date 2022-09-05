package com.vaadin.example.sightseeing.components;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.shared.Registration;

@Tag("geo-location")
@JsModule("components/geo-location.ts")
public class GeoLocation extends Component {

    public GeoLocation() {

    }

    public Registration addChangeListener(ComponentEventListener<GeoLocationReceivedEvent> listener) {
        return addListener(GeoLocationReceivedEvent.class, listener);
    }

    @ClientCallable
    private void sendGeoLocation() {

    }

    @DomEvent("geo-location-received")
    public static class GeoLocationReceivedEvent extends ComponentEvent<GeoLocation> {

        private final Double latitude;
        private final Double longitude;

        public GeoLocationReceivedEvent(GeoLocation source, boolean fromClient,
                                        @EventData("event.detail.latitude") Double latitude,
                                        @EventData("event.detail.longitude") Double longitude) {
            super(source, fromClient);
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public Double getLatitude() {
            return latitude;
        }

        public Double getLongitude() {
            return longitude;
        }
    }

}
