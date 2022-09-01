package com.vaadin.example.sightseeing.data.entity;

import dev.hilla.Nonnull;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
public class Tag extends AbstractEntity {

    @ManyToOne
    @Nonnull
    @JsonIgnore // To avoid loop
    private Place place;
    @Nonnull
    private String name;
    @Nonnull
    private String val;
    @Nonnull
    private boolean enabled = true;

    public Tag() {
    }

    public Tag(Place place, String name, String val) {
        this.place = place;
        this.name = name;
        this.val = val;
    }

    public Place getPlace() {
        return place;
    }
    public void setPlace(Place place) {
        this.place = place;
    }

    // TODO: report that @JsonSerialize in getter does not work in hilla,
    // it needs to define the private property
    @Nonnull
    private String placeName;

    @JsonSerialize
    public String getPlaceName() {
        return this.place.getName();
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getVal() {
        return val;
    }
    public void setVal(String val) {
        this.val = val;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    @Override
    public String toString() {
        return "[place=" + place.getId()
                + ", name=" + name + ", val=" + val + ", enabled=" + enabled + "]";
    }
}
