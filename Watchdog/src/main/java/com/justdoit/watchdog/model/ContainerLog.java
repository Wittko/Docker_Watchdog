package com.justdoit.watchdog.model;
import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;


@Entity
@Table(name="containerLogs")
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
    @Column(name="cpuPercent")
    private double cpuPercent;
    @Column(name="memoryUsageMb")
    private double memoryUsageMb;
    @UpdateTimestamp
    @Column(name="loggedAt")
    private LocalDateTime loggedAt;

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

    public double getCpuPercent() {
        return cpuPercent;
    }

    public void setCpuPercent(double cpuPercent) {
        this.cpuPercent = cpuPercent;
    }

    public double getMemoryUsageMb() {
        return memoryUsageMb;
    }

    public void setMemoryUsageMb(double memoryUsageMb) {
        this.memoryUsageMb = memoryUsageMb;
    }

    public LocalDateTime getLoggedAt() {
        return loggedAt;
    }

    public void setLoggedAt(LocalDateTime loggedAt) {
        this.loggedAt = loggedAt;
    }

}
