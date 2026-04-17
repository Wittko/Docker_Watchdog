package com.justdoit.watchdog.model;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;


@Entity
@Data
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
}
