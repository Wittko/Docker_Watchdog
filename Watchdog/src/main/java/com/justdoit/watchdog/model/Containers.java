package com.justdoit.watchdog.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name="containers")
public class Containers {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name= "count" )
    private int count;
    @Column(name="dockerId", unique = true )
    private String dockerId;
    @Column(name="name", unique = true )
    private String name;
    @Column(name="image")
    private String image;
    @CreationTimestamp
    @Column(name="discoveredAt", updatable = false)
    private LocalDateTime discoveredAt;

    public Containers() {

    }
}

