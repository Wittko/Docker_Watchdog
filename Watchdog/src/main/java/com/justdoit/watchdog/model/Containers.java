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
    @Column(name="dockerId" )
    private String dockerId;
    @Column(name="name" )
    private String name;
    @Column(name="image")
    private String image;
    @CreationTimestamp
    @Column(name="discovered_at", updatable = false)
    private LocalDateTime discovered_at;

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

    public void setDockerId(String containerId) {
        this.dockerId = containerId;
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

    public LocalDateTime getDiscovered_at() {
        return discovered_at;
    }

    public void setDiscovered_at(LocalDateTime discovered_at) {
        this.discovered_at = discovered_at;
    }

}

