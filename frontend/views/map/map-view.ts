import { html } from 'lit';
import { customElement, query } from 'lit/decorators.js';
import { View } from '../../views/view';
import "@vaadin/map";
import TileLayer from "ol/layer/Tile";
import OSM from "ol/source/OSM";
import OlView from "ol/View";
import { setUserProjection } from 'ol/proj';
import type { Map } from '@vaadin/map';
import * as MapEndpoint from 'Frontend/generated/MapEndpoint';

@customElement('map-view')
export class MapView extends View {

  @query('#map')
  private map!: Map;

  async connectedCallback() {
    super.connectedCallback();

    customElements.whenDefined("vaadin-map").then(async () => {
      setUserProjection('EPSG:4326');
      this.map.configuration.addLayer(new TileLayer({
        source: new OSM()
      }));
      this.map.configuration.setView(new OlView({
        center: await MapEndpoint.getCenter(),
        zoom: 12
      })); 
    });
  }

  render() {
    return html`
      <vaadin-map id="map" theme="borderless"></vaadin-map>
    `;
  }
}
