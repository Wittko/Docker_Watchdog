package com.justdoit.watchdog.model;

import lombok.Data;

@Data
public class DashboardDTO {
    private String name;
    private String image;
    private String status;
    private double cpu_percent;
    private double memory_usage_mb;
}
