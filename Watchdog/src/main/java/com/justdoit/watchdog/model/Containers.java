package com.justdoit.watchdog.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="containers")
public class Containers {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name= "count" )
    private int count;
    @Column(name="dockerId", unique = true )
    private String dockerId;
    @Column(name="name" )
    private String name;
    @Column(name="image")
    private String image;
    @CreationTimestamp
    @Column(name="discoveredAt", updatable = false)
    private LocalDateTime discoveredAt;

    public Containers() {

    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDockerId() {
        return dockerId;
    }

    public void setDockerId(String dockerId) {
        this.dockerId = dockerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public LocalDateTime getDiscoveredAt() {
        return discoveredAt;
    }

    public void setDiscoveredAt(LocalDateTime discoveredAt) {
        this.discoveredAt = discoveredAt;
    }

}

