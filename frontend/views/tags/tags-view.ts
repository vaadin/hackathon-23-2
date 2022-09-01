import { Binder, field } from '@hilla/form';
import { EndpointError } from '@hilla/frontend';
import '@vaadin/button';
import '@vaadin/date-picker';
import '@vaadin/date-time-picker';
import '@vaadin/form-layout';
import '@vaadin/grid';
import { Grid, GridDataProviderCallback, GridDataProviderParams } from '@vaadin/grid';
import { columnBodyRenderer } from '@vaadin/grid/lit';
import '@vaadin/grid/vaadin-grid-sort-column';
import '@vaadin/horizontal-layout';
import '@vaadin/icon';
import '@vaadin/icons';
import '@vaadin/notification';
import { Notification } from '@vaadin/notification';
import '@vaadin/polymer-legacy-adapter';
import '@vaadin/split-layout';
import '@vaadin/text-field';
import '@vaadin/upload';
import '@vaadin/vaadin-icons';
import Tag from 'Frontend/generated/com/vaadin/example/sightseeing/data/entity/Tag';
import TagModel from 'Frontend/generated/com/vaadin/example/sightseeing/data/entity/TagModel';
import Sort from 'Frontend/generated/dev/hilla/mappedtypes/Sort';
import Direction from 'Frontend/generated/org/springframework/data/domain/Sort/Direction';
import * as TagEndpoint from 'Frontend/generated/TagEndpoint';
import { html } from 'lit';
import { customElement, property, query } from 'lit/decorators.js';
import { View } from '../view';

@customElement('tags-view')
export class TagsView extends View {
  @query('#grid')
  private grid!: Grid;

  @property({ type: Number })
  private gridSize = 0;

  private gridDataProvider = this.getGridData.bind(this);

  private binder = new Binder<Tag, TagModel>(this, TagModel);

  render() {
    return html`
      <vaadin-split-layout>
        <div class="grid-wrapper">
          <vaadin-grid
            id="grid"
            theme="no-border"
            .size=${this.gridSize}
            .dataProvider=${this.gridDataProvider}
            @active-item-changed=${this.itemSelected}
          >
            <vaadin-grid-sort-column path="placeName" auto-width></vaadin-grid-sort-column>
            <vaadin-grid-sort-column path="name" auto-width></vaadin-grid-sort-column>
            <vaadin-grid-sort-column path="val" auto-width></vaadin-grid-sort-column>
            <vaadin-grid-column
              path="enabled"
              auto-width
              ${columnBodyRenderer<Tag>((item) =>
                item.enabled
                  ? html`<vaadin-icon
                      icon="vaadin:check"
                      style="width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: var(--lumo-primary-text-color);"
                    >
                    </vaadin-icon>`
                  : html`<vaadin-icon
                      icon="vaadin:minus"
                      style="width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: var(--lumo-disabled-text-color);"
                    >
                    </vaadin-icon>`
              )}
            ></vaadin-grid-column>
          </vaadin-grid>
        </div>
        <div class="editor-layout">
          <div class="editor">
            <vaadin-form-layout
              ><vaadin-text-field label="Place" id="place" ${field(this.binder.model.placeName)} readonly></vaadin-text-field
              ><vaadin-text-field label="Name" id="name" ${field(this.binder.model.name)}></vaadin-text-field
              ><vaadin-text-field label="Val" id="val" ${field(this.binder.model.val)}></vaadin-text-field
              ><vaadin-checkbox id="enabled" ${field(this.binder.model.enabled)} label="Enabled"></vaadin-checkbox
            ></vaadin-form-layout>
          </div>
          <vaadin-horizontal-layout class="button-layout">
            <vaadin-button theme="primary" @click=${this.save}>Save</vaadin-button>
            <vaadin-button theme="tertiary" @click=${this.cancel}>Cancel</vaadin-button>
          </vaadin-horizontal-layout>
        </div>
      </vaadin-split-layout>
    `;
  }

  private async getGridData(params: GridDataProviderParams<Tag>, callback: GridDataProviderCallback<Tag | undefined>) {
    const sort: Sort = {
      orders: params.sortOrders.map((order) => ({
        property: order.path,
        direction: order.direction == 'asc' ? Direction.ASC : Direction.DESC,
        ignoreCase: false,
      })),
    };
    const data = await TagEndpoint.list({ pageNumber: params.page, pageSize: params.pageSize, sort });
    callback(data);
  }

  async connectedCallback() {
    super.connectedCallback();
    this.gridSize = (await TagEndpoint.count()) ?? 0;
  }

  private async itemSelected(event: CustomEvent) {
    const item: Tag = event.detail.value as Tag;
    this.grid.selectedItems = item ? [item] : [];

    if (item) {
      const fromBackend = await TagEndpoint.get(item.id!);
      fromBackend ? this.binder.read(fromBackend) : this.refreshGrid();
    } else {
      this.clearForm();
    }
  }

  private async save() {
    try {
      const isNew = !this.binder.value.id;
      await this.binder.submitTo(TagEndpoint.update);
      if (isNew) {
        // We added a new item
        this.gridSize++;
      }
      this.clearForm();
      this.refreshGrid();
      Notification.show(`Tag details stored.`, { position: 'bottom-start' });
    } catch (error: any) {
      if (error instanceof EndpointError) {
        Notification.show(`Server error. ${error.message}`, { theme: 'error', position: 'bottom-start' });
      } else {
        throw error;
      }
    }
  }

  private cancel() {
    this.grid.activeItem = undefined;
  }

  private clearForm() {
    this.binder.clear();
  }

  private refreshGrid() {
    this.grid.selectedItems = [];
    this.grid.clearCache();
  }
}
