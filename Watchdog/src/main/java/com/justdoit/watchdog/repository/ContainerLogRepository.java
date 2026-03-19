package com.justdoit.watchdog.repository;

import com.justdoit.watchdog.model.Containers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContainerLogRepository extends JpaRepository<Containers, Integer>{}