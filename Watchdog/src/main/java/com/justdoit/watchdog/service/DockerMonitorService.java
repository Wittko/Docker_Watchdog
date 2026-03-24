package com.justdoit.watchdog.service;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.justdoit.watchdog.model.ContainerLog;
import com.justdoit.watchdog.model.Containers;
import com.justdoit.watchdog.repository.ContainerLogRepository;
import com.justdoit.watchdog.repository.ContainerRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DockerMonitorService {

    private final ContainerRepository containerRepository;
    private final ContainerLogRepository containerLogRepository;
    private final DockerClient dockerClient;

    public DockerMonitorService(ContainerRepository containerRepository, ContainerLogRepository containerLogRepository) {
        this.containerRepository = containerRepository;
        this.containerLogRepository = containerLogRepository;

        System.out.println("Starte Docker-Watchdog Verbindungstest");
        // Konfiguration für Unix-Socket
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
//                .withDockerHost("unix:///var/run/docker.sock") #Unix socket currently not working bc Win/WSL coms
                .withDockerHost("tcp://localhost:2375")
                .build();

        // Transport Layer http5Client
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .build();

        // Docker Client instanziieren
        this.dockerClient = DockerClientBuilder.getInstance(config)
                .withDockerHttpClient(httpClient)
                .build();
    }

    @Scheduled(fixedRate = 30000, initialDelay = 5000)
    @Transactional
    public void checkContainers(){
        try {
            List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();

            System.out.println("Erfolg! " + containers.size() + " Container gefunden");

            for (Container dockerContainer : containers) {
                Containers containerEntity = containerRepository.findByDockerId(dockerContainer.getId())
                        .orElse(new Containers());
                //Docker ID Abfragen
                containerEntity.setDockerId(dockerContainer.getId());
                //Dockername abfragen
                String conName = dockerContainer.getNames()[0].replace("/", "");
                containerEntity.setName(conName);
                //Docker Image abfragen
                containerEntity.setImage(dockerContainer.getImage());
                //Container Repository speichert
                containerEntity = containerRepository.save(containerEntity);

                //Log-Abteilung
                ContainerLog log = new ContainerLog();
                log.setContainer(containerEntity);
                log.setStatus(dockerContainer.getState());
                log.setCpuPercent(0.0); //ToDo - Abfragelogik ergänzen
                log.setMemoryUsageMb(0.0);
                containerLogRepository.save(log);

                System.out.println("Container gespeichert: " + conName + " [" + dockerContainer.getState() + "]");

//                String name = String.join(", ", container.getNames());
//                String status = container.getState();
//                System.out.println(">>Name: " + name + "| Status: [" + status + "]");
            }
        } catch (Exception e) {
            System.err.println("Fehler bei der Kommunikation mit Docker:");
            e.printStackTrace();
        }
    }
}
