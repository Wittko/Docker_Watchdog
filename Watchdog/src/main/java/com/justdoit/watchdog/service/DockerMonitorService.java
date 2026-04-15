package com.justdoit.watchdog.service;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Statistics;
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
import java.util.concurrent.TimeUnit;


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

    @Scheduled(fixedRate = 60000, initialDelay = 2000)
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
                //Statistics Code
                String currentState = dockerContainer.getState();
                if ("exited".equalsIgnoreCase(currentState)) {
                    try {
                        var inspect = dockerClient.inspectContainerCmd(dockerContainer.getId()).exec();
                        int exitCode = inspect.getState().getExitCode();

                        if (exitCode != 0) {
                            currentState = "ERROR (" + exitCode + ")";
                        }
                    } catch (Exception e) {
                        currentState = "ERROR (" + e.getMessage() + ")";
                    }
                }
                System.out.println("Container: " + conName + " | Status: " + currentState);
                    //Placeholder für Statistics (onNext)
                final Statistics[] statsHolder = new Statistics[1];
                var command = dockerClient.statsCmd(dockerContainer.getId()).withNoStream(true);
                ResultCallback.Adapter<Statistics> callback = new ResultCallback.Adapter<>(){
                    @Override
                    public void onNext(Statistics statistics) {
                        statsHolder[0] = statistics;
                        super.onNext(statistics);
                    }
                };
                command.exec(callback);
                // Memory Usage
                Long memUsage = 0L;
                try {
                    callback.awaitCompletion(5, TimeUnit.SECONDS);
                    Statistics stats = statsHolder[0];

                    if (stats != null && stats.getMemoryStats() != null && stats.getMemoryStats().getUsage() != null) {
                        memUsage = stats.getMemoryStats().getUsage();
                        System.out.println(conName + " - Speicherverbrauch in Bytes: " + memUsage);
                    }
                } catch (InterruptedException e) {
                    System.err.println("Abbruch beim Warten auf Stats");
                }
                double memoryMb = memUsage / (1024.0 * 1024.0);
                double cpuUsage = 0.0;
                try {
                    callback.awaitCompletion(5, TimeUnit.SECONDS);
                    Statistics stats = statsHolder[0];

                    if (stats == null) {
                        System.out.println("Timeout: Keine CPU Stats");
                        return;
                    }

                    if (
                            stats.getCpuStats() != null &&
                            stats.getPreCpuStats() != null &&
                            stats.getCpuStats().getCpuUsage() != null &&
                            stats.getPreCpuStats().getCpuUsage() != null &&
                            stats.getCpuStats().getOnlineCpus() != null){

                        double cpuDelta = (double) stats.getCpuStats().getCpuUsage().getTotalUsage() - stats.getPreCpuStats().getCpuUsage().getTotalUsage();
                        double systemDelta = (double) stats.getCpuStats().getSystemCpuUsage() - stats.getPreCpuStats().getSystemCpuUsage();
                        double onlineCpus = stats.getCpuStats().getOnlineCpus();
                        if (cpuDelta > 0.0 && systemDelta > 0.0) {
                            cpuUsage = (cpuDelta/systemDelta) * onlineCpus * 100.0;
                        }
                    }
                } catch (InterruptedException e) {
                    System.out.println("Abbruch beim CPU-Scan");
                }
                double calcCpu = cpuUsage;

                //Log-Abteilung
                ContainerLog log = new ContainerLog();
                log.setContainer(containerEntity);
                log.setStatus(dockerContainer.getState());
                log.setCpuPercent(calcCpu); //ToDo - Abfragelogik ergänzen
                log.setMemoryUsageMb(memoryMb);
                containerLogRepository.save(log);

                System.out.println("Container gespeichert: " + conName + " [" + dockerContainer.getState() + "]");}

        } catch (Exception e) {
            System.err.println("Fehler bei der Kommunikation mit Docker:");
            e.printStackTrace();
        }
    }
}
