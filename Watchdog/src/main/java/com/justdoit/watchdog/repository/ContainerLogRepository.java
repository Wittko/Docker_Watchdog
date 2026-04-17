package com.justdoit.watchdog.repository;
import com.github.dockerjava.api.model.Container;
import com.justdoit.watchdog.model.ContainerLog;
import com.justdoit.watchdog.model.Containers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ContainerLogRepository extends JpaRepository<ContainerLog, Integer> {
    ContainerLog findFirstByContainerOrderByLoggedAtDesc(Containers container);
    List<ContainerLog> findTop100ByContainerOrderByLoggedAtDesc(Containers container);
}