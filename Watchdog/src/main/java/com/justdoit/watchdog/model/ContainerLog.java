package com.justdoit.watchdog.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="container_logs")
public class ContainerLog {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="logId")
    private int logId;
    @ManyToOne
    @JoinColumn(name = "containerId", nullable = false)
    private Containers container;
    @Column(name="status")
    private String status;
    @Column(name="cpu_percent")
    private double cpu_percent;
    @Column(name="memory_usage_mb")
    private double memory_usage_mb;
    @UpdateTimestamp
    @Column(name="logged_at")
    private LocalDateTime logged_at;

    public ContainerLog() {

    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public Containers getContainer() {
        return container;
    }

    public void setContainer(Containers container) {
        this.container = container;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getCpu_percent() {
        return cpu_percent;
    }

    public void setCpu_percent(double cpu_percent) {
        this.cpu_percent = cpu_percent;
    }

    public double getMemory_usage_mb() {
        return memory_usage_mb;
    }

    public void setMemory_usage_mb(double memory_usage_mb) {
        this.memory_usage_mb = memory_usage_mb;
    }

    public LocalDateTime getLogged_at() {
        return logged_at;
    }

    public void setLogged_at(LocalDateTime logged_at) {
        this.logged_at = logged_at;
    }

}
