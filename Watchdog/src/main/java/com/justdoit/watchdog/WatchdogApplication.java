package com.justdoit.watchdog;
import com.justdoit.watchdog.service.DockerMonitorService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class WatchdogApplication implements CommandLineRunner {

    private final DockerMonitorService dockerMonitorService;

    public WatchdogApplication(DockerMonitorService dockerMonitorService) {
        this.dockerMonitorService = dockerMonitorService;
    }

    static void main(String[] args) {
        SpringApplication.run(WatchdogApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        dockerMonitorService.checkContainers();
    }
}
