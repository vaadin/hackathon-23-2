package com.vaadin.example.sightseeing.data.entity;

import dev.hilla.Nonnull;
import javax.persistence.Entity;

@Entity
public class Tag extends AbstractEntity {

    @Nonnull
    private Integer place;
    @Nonnull
    private String name;
    @Nonnull
    private String val;
    @Nonnull
    private boolean enabled;

    public Integer getPlace() {
        return place;
    }
    public void setPlace(Integer place) {
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

}
