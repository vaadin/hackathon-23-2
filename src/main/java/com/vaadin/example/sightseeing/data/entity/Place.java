package com.vaadin.example.sightseeing.data.entity;

import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

import com.vaadin.flow.component.map.configuration.Coordinate;

@Entity
public class Place extends AbstractEntity {


    private String name;
    private Double x;
    private Double y;

    final String[] tagNames = {"name:es", "name:en", "name", "description:es", "description:en", "historic", "religion"};

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @OrderColumn
    @JoinColumn
    private Set<Tag> tags;

    private Long oid;

    private LocalDateTime updated;

    private boolean enabled = true;

    public Place() {
    }

    public Place(Long id, double x, double y, Map<String, String> tags, Instant instant) {
       this.oid = id;
       if (instant != null) {
           this.updated = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
       }
       this.x = x;
       this.y = y;
       for (String k : tagNames) {
           if (tags.get(k) != null) {
               this.name = tags.get(k);
               break;
           }
       }
       this.tags = tags.entrySet().stream().map(e -> new Tag(this, e.getKey(), e.getValue())).collect(Collectors.toSet());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public Long getOid() {
        return oid;
    }

    public void setOid(Long oid) {
        this.oid = oid;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Coordinate getCoordinate() {
        if (x == null || y == null) {
            return null;
        }
        return new Coordinate(x, y);
    }

    public void setCoordinate(Coordinate coordinate) {
        if (coordinate == null) {
            x = null;
            y = null;
        } else {
            x = coordinate.getX();
            y = coordinate.getY();
        }
    }

    @Override
    public String toString() {
        return "Place [id= " + getId() + "name=" + name + ", x=" + x + ", y=" + y +  ", tags=" + tags + ", oid=" + oid
                + ", updated=" + updated + ", enabled=" + enabled + "]";
    }

}
