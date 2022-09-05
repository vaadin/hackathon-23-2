import {html, LitElement} from "lit";
import {customElement, property} from "lit/decorators.js";

@customElement("geo-location")
export class GeoLocation extends LitElement {
    @property({type: Object})
    private position?: GeolocationPosition;

    @property({type: Object})
    private error?: GeolocationPositionError;

    private watchId = -1;

    render() {
        return html`
        `;
    }

    connectedCallback() {
        super.connectedCallback();

        // https://developer.mozilla.org/en-US/docs/Web/API/PositionOptions
        const options: PositionOptions = {
            enableHighAccuracy: true,
            timeout: 10 * 1000, // 10 seconds
            maximumAge: 100, // 100ms old values are still OK
        };
        // https://developer.mozilla.org/en-US/docs/Web/API/Geolocation/watchPosition
        this.watchId = navigator.geolocation.watchPosition(
            (position: GeolocationPosition) => {
                this.position = position;
                this.error = undefined;
                this.dispatchEvent(new CustomEvent('geo-location-received', {
                    detail: {
                        latitude: position.coords.latitude,
                        longitude: position.coords.longitude,
                    }
                }));
            },
            (error: GeolocationPositionError) => {
                this.position = undefined;
                this.error = error;
            },
            options
        );
    }

    disconnectedCallback() {
        navigator.geolocation.clearWatch(this.watchId);
        super.disconnectedCallback();
    }

}
