package com.vaadin.example.sightseeing.data.entity;

import dev.hilla.Nonnull;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Tag extends AbstractEntity {

    @ManyToOne
    @Nonnull
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
        if (place.getName() == null) {

        }
    }

    public Place getPlace() {
        return place;
    }
    public void setPlace(Place place) {
        this.place = place;
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
