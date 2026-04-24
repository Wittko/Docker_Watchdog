package com.justdoit.watchdog.service;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Statistics;
import com.justdoit.watchdog.model.ContainerLog;
import com.justdoit.watchdog.model.Containers;
import com.justdoit.watchdog.repository.ContainerLogRepository;
import com.justdoit.watchdog.repository.ContainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DockerMonitorService {

    private final ContainerRepository containerRepository;
    private final ContainerLogRepository containerLogRepository;
    private final DockerClient dockerClient;

    @Scheduled(fixedRate = 60000, initialDelay = 1000)
    @Transactional
    public void checkContainers() {
        try {
            List<Container> containers = dockerClient.listContainersCmd().withShowAll(false).exec();
            log.info("Erfolg! {} Container gefunden", containers.size());

            for (Container dockerContainer : containers) {
                processSingleContainer(dockerContainer);
            }
        } catch (Exception e) {
            log.error("Fehler bei Kommunikation mit Docker", e);
        }
    }

    private void processSingleContainer(Container dockerContainer) {
        try {
            Containers containerEntity = saveOrUpdateContainer(dockerContainer);
            String status = determineContainerStatus(dockerContainer);
            ContainerStats stats = fetchContainerStats(dockerContainer.getId());
            saveContainerLog(containerEntity, status, stats);

            log.debug("Container verarbeitet {} [{}]", containerEntity.getName(), status);
        } catch (Exception e) {
            log.error("Fehler beim Verarbeiten von Container {}", dockerContainer.getId(), e);
        }
    }

    private Containers saveOrUpdateContainer(Container dockerContainer) {
        Containers entity = containerRepository.findByDockerId(dockerContainer.getId())
                .orElse(new Containers());

        entity.setDockerId(dockerContainer.getId());
        entity.setName(dockerContainer.getNames()[0].replace("/", ""));
        entity.setImage(dockerContainer.getImage());

        return containerRepository.save(entity);
    }

    private String determineContainerStatus(Container dockerContainer) {
        String state = dockerContainer.getState();
        if (!"exited".equalsIgnoreCase(state)) {
            return state;
        }

        try {
            var inspect = dockerClient.inspectContainerCmd(dockerContainer.getId()).exec();
            int exitCode = inspect.getState().getExitCode();
            return exitCode != 0 ? "ERROR (" + exitCode + ")" : state;
        } catch (Exception e) {
            return "ERROR (" + e.getMessage() + ")";
        }
    }

    private record ContainerStats(double memoryMb, double cpuUsage) {}

    private ContainerStats fetchContainerStats(String containerId) {
        final Statistics[] statsHolder = new Statistics[1];
        var command = dockerClient.statsCmd(containerId).withNoStream(true);

        ResultCallback.Adapter<Statistics> callback = new ResultCallback.Adapter<>() {
            @Override
            public void onNext(Statistics statistics) {
                statsHolder[0] = statistics;
                super.onNext(statistics);
            }
        };

        command.exec(callback);

        try {
            callback.awaitCompletion(5, TimeUnit.SECONDS);
            Statistics stats = statsHolder[0];

            if (stats == null) {
                log.warn("Timeout: Keine Stats für Container {}", containerId);
                return new ContainerStats(0.0, 0.0);
            }

            double memoryMb = calculateMemoryMb(stats);
            double cpuUsage = calculateCpuUsage(stats);

            return new ContainerStats(memoryMb, cpuUsage);

        } catch (InterruptedException e) {
            log.error("Abbruch beim Warten auf Stats von Container {}", containerId);
            Thread.currentThread().interrupt();
            return new ContainerStats(0.0, 0.0);
        }
    }

    private double calculateMemoryMb(Statistics stats) {
        if (stats.getMemoryStats() == null || stats.getMemoryStats().getUsage() == null) {
            return 0.0;
        }
        return stats.getMemoryStats().getUsage() / (1024.0 * 1024.0);
    }

    private double calculateCpuUsage(Statistics stats) {
        if (stats.getCpuStats() == null || stats.getPreCpuStats() == null ||
                stats.getCpuStats().getCpuUsage() == null || stats.getPreCpuStats().getCpuUsage() == null ||
                stats.getCpuStats().getOnlineCpus() == null) {
            return 0.0;
        }

        double cpuDelta = (double) stats.getCpuStats().getCpuUsage().getTotalUsage() - stats.getPreCpuStats().getCpuUsage().getTotalUsage();
        double systemDelta = (double) stats.getCpuStats().getSystemCpuUsage() - stats.getPreCpuStats().getSystemCpuUsage();
        double onlineCpus = stats.getCpuStats().getOnlineCpus();

        if (cpuDelta > 0 && systemDelta > 0.0) {
            return (cpuDelta / systemDelta) * onlineCpus * 100.0;
        }
        return 0.0;
    }

    private void saveContainerLog(Containers entity, String status, ContainerStats stats) {
        ContainerLog log = new ContainerLog();
        log.setContainer(entity);
        log.setStatus(status);
        log.setCpuPercent(stats.cpuUsage());
        log.setMemoryUsageMb(stats.memoryMb());
        containerLogRepository.save(log);
    }
}