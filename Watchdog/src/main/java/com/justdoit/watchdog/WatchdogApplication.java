package com.justdoit.watchdog;
import com.justdoit.watchdog.service.DockerMonitorService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@SpringBootApplication
@EnableScheduling
public class WatchdogApplication{

    static void main(String[] args) {
        SpringApplication.run(WatchdogApplication.class, args);
    }
}
