package com.justdoit.watchdog;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class WatchdogApplication{

    static void main(String[] args) {
        SpringApplication.run(WatchdogApplication.class, args);
    }
}
