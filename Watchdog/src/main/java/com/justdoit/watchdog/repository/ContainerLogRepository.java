package com.justdoit.watchdog.repository;

import com.justdoit.watchdog.model.ContainerLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContainerLogRepository extends JpaRepository<ContainerLog, Integer>{}