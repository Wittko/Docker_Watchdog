package com.justdoit.watchdog;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

import java.util.List;

public class Main {
    public static void main(String[] args) {
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

        // Docker Client instanzieren
        DockerClient dockerClient = DockerClientBuilder.getInstance(config)
                .withDockerHttpClient(httpClient)
                .build();

        try {
            List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();

            System.out.println("Erfolg! " + containers.size() + " Container gefunden");

            for (Container container : containers) {
                String name = String.join(", ", container.getNames());
                String status = container.getState();
                System.out.println(">>Name: " + name + "| Status: [" + status + "]");
            }
        } catch (Exception e) {
            System.err.println("Fehler bei der Kommunikation mit Docker:");
            e.printStackTrace();
        }
    }

}
