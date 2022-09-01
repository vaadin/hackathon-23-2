package com.vaadin.example.sightseeing.data.entity;

import dev.hilla.Nonnull;
import java.time.LocalDateTime;
import javax.persistence.Entity;

@Entity
public class Place extends AbstractEntity {

    @Nonnull
    private String name;
    @Nonnull
    private Integer x;
    @Nonnull
    private Integer y;
    @Nonnull
    private String tags;
    private LocalDateTime updated;
    @Nonnull
    private Integer oid;
    @Nonnull
    private boolean enabled;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getX() {
        return x;
    }
    public void setX(Integer x) {
        this.x = x;
    }
    public Integer getY() {
        return y;
    }
    public void setY(Integer y) {
        this.y = y;
    }
    public String getTags() {
        return tags;
    }
    public void setTags(String tags) {
        this.tags = tags;
    }
    public LocalDateTime getUpdated() {
        return updated;
    }
    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }
    public Integer getOid() {
        return oid;
    }
    public void setOid(Integer oid) {
        this.oid = oid;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
